/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import com.crunchynoodles.util.SynchronizedByteBuffer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Future;

import com.google.common.collect.Queues;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import org.embeddedrailroad.eri.layoutio.LayoutTimeoutManager;


/***
 *   Provides a state-machine to poll units on a CMRI bank via some comms-channel.
 *   Model provides initialize strings for units, and a place to dump input-data updates.
 *   Instance has a little worker-thread running in a local-class-instance.
 * <p>
 *   Note: Almost all the public methods are {@code synchronized}.
 *   Happily, these methods can call other {@code synchronized} methods of the same class.
 *   That said, please don't use instances of {@link CmriPollMachine} for locking in a client class.
 *   The {@code synchronized} methods already to that!
 *
 * @author brian
 */
public class CmriPollMachine
            implements UncaughtExceptionHandler
{
    public CmriPollMachine( SerialPort serial, int baudRate, CmriLayoutModelImpl model )
    {
        this.m_model = model;
        this.m_recovery_rate = CmriSerialLayoutTransport.DEFAULT_DISCOVERY_RATE;
        this.m_port = serial;
        this.m_baud_rate = baudRate;

        this.m_consecutive_missed_polls = new int[ CMRI_HIGHEST_POLL_ADDR + 1 ];

        // ``"Synchronized" [collection] classes can be useful when you need to prevent all access to
        //   a collection via a single lock, at the expense of poorer scalability.''
        this.m_active_queue = Queues.synchronizedQueue( new java.util.LinkedList<Integer>() );
        this.m_revive_queue = Queues.synchronizedQueue( new java.util.LinkedList<Integer>() );
    }

    //----------------------------  BEAN THINGS  ------------------------------

    /***
     *  Maximum time, in milliseconds, to wait for first char in response from a unit after
     *  a query message has been sent out.
     *  If the first char isn't received in said microseconds, the unit is declared "non-responding."
     *
     * @param millisecs max wait time after transmitting query.
     */
    public void setRxTurnaroundTimeout( int millisecs )
    {
        m_response_wait = millisecs;
    }

    public int getRxTurnaroundTimeout()
    {
        return this.m_response_wait;
    }

    /***
     *  Return the timeout in milliseconds to wait for first byte of a response starting
     *  from when the first byte is sent out.
     *  Takes size of TX packet, which assumes it is buffered by OS and is right now
     *  being sent out.
     *  In modern OSes, the serial or USB driver has buffered the packet to send.
     *
     * @param sizeSent how many bytes will be sent.
     * @return milliseconds to wait, based on {@link #setRxTurnaroundTimeout(int) } value.
     */
    public int getTxToFirstRxTimeout( int sizeSent )
    {
        int  wait_xmit = (int) ( ((sizeSent + 2) / (m_baud_rate / 10.0)) * 1000 );
        int  wait_rec  = getRxTurnaroundTimeout();

        return wait_xmit + wait_rec;
    }

    /***
     *  Set recovery rate to re-try units that are not responding.
     *  After every poll cycle, the recovery rate is added to an accumulator.
     *  Once 1.0 is reached ( or exceeded ), communication with a unit is attempted.
     *
     * @param rr value more than 0.0 but not above 1.0.
     * @throws IllegalArgumentException if {@link rr} less-than or equal to 0, or above 1.0
     */
    public void setRecoveryRate( double rr )
    {
        if( rr <= 0.0 || rr > 1.0 )
            throw new IllegalArgumentException( "recovery rate must be (0.0 .. 1.0]" );

        this.m_recovery_rate = (float) rr;
    }

    public float getRecoveryRate()
    {
        return this.m_recovery_rate;
    }

    /***
     *  Put some unit on the revival work list.
     *  If currently polling, then unit is demoted to re-initialization.
     *  E.g. adding the same unit-address twice, will first put it on the poll list,
     *  and then move to the "revive list" on the second call here.
     *
     * @param unitAddr
     */
    public synchronized void addUnitToPollingList( int unitAddr )
    {
        if( m_active_queue.contains( unitAddr ) )
        {
            m_active_queue.remove( unitAddr );
        }
        if( ! m_revive_queue.contains( unitAddr ) )
        {
            m_revive_queue.add( unitAddr );
        }
    }

    /***
     *  Stop talking with some unit in the bank.
     *  Removes unit-address from both the active polling list and the revive list.
     *
     * @param unitAddr which to remove
     */
    public synchronized void removeUnitFromPollingList( int unitAddr )
    {
        m_active_queue.remove( unitAddr );
        m_revive_queue.remove( unitAddr );
    }

    /***
     *  Return set of known unit addresses.
     *  Normally, list is just units that are actively communicating.
     *
     * @param includeNonResponding include those not responding
     * @return Set of unit addresses.
     */
    public synchronized Set<Integer>  getKnownUnits( boolean includeNonResponding )
    {
        HashSet<Integer>  known = new HashSet<Integer>();

        known.addAll( m_active_queue );

        if( includeNonResponding )
        {
            known.addAll( m_revive_queue );
        }

        return( known );
    }

    /***
     *  Count another missed response from a unit ; too many and try to re-INIT unit
     * @param addr polling address, 0 to 255
     */
    private void _missedPoll( int addr )
    {
        m_consecutive_missed_polls[ addr ] += 1;
        if( m_consecutive_missed_polls[ addr ] > PROP_GONE_TOO_LONG_REINIT )
        {
            LOG.log( Level.WARNING, "Unit #{0} hasn't responded in a while, will re-INIT.", addr );
            m_consecutive_missed_polls[ addr ] = 0;

            //  Since unit it on polling list, call addUnitToReviveList() will demote to revive list.
            addUnitToPollingList( addr );
        }
    }

    //------------------------  WORKER THREAD SUPPORT  ------------------------

    public boolean isPolling()
    {
        return this.m_polling;
    }

    /***
     *  Start or stop polling, on first start creates the worker thread.
     *  Creation is automatic; however, for worker-thread exit-join, the client must call {@link #shutdown()}
     *  explicitly.
     *
     * @param goPoll true to start, false to stop.
     */
    public synchronized void setPolling( boolean goPoll )
    {

        if( goPoll != m_polling )
        {
            //  On change, either start or stop the worker thread that does the polling.
            if( goPoll )
            {
                if( m_worker == null )
                {
                    m_worker = new CmriSerialPollingWorker();
                }

                if( m_thread == null )
                {
                    m_thread = new Thread( m_worker );
                    m_thread.setDaemon( true );
                    m_thread.setName( m_worker.getClass().getSimpleName() + " on " + m_port.getName() );
                    m_thread.setUncaughtExceptionHandler( this );

                    m_thread.start();
                }

                m_worker.setOkToPoll( true );
            }
            else
            {
                m_worker.setOkToPoll( false );
            }

            //  It changed, so update...
            m_polling = goPoll;
        }

    }

    /***
     *  Stop polling and kill the polling thread, which shuts down this transport.
     *  Stop any worker thread from polling and then do {@link Thread.join()} to reclaim it.
     *  Will wait just a few seconds for worker-thread to exit itself before returning to caller.
     */
    public synchronized void shutdown()
    {
        //  Stop polling, just in case...
        if( m_polling == true )
        {
            setPolling( false );
        }

        if( m_thread != null )
        {
            try {
                m_worker.stopWorkerThread();
                m_thread.join( 3500 );      // 3500 = 3.5 seconds.

                if( m_thread.isAlive() )
                {
                    //  Hmmm... it didn't stop for use gently, so send INTR and wait again.
                    //  See http://docs.oracle.com/javase/tutorial/essential/concurrency/simple.html
                    m_thread.interrupt();
                    m_thread.join( 3000 );
                }
            }
            catch( InterruptedException ex ) {
                m_thread.interrupt();
            }

            m_worker = null;
            m_thread = null;
        }

    }

    /***
     *  Catch-before-falling our worker thread.
     *  Method invoked when the given thread terminates due to the
     *  given uncaught exception.
     *
     * <p>"Any exception thrown by this method will be ignored by the
     * Java Virtual Machine."
     *
     * @param t the thread
     * @param e the exception that was not caught by the thread itself.
     */
    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        if( t == m_thread )
        {
            shutdown();
        }
    }

    //---------------------  POLLING MACHINE (INTERNAL)  ----------------------

    /***
     *  Class-type that runs state-machine to poll units in a bank.
     *
     * <p> Note: By Java semantics, inner-class cannot have {@code static} methods.
     *
     * <p> {@link Runnable} : See http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
     */
    class CmriSerialPollingWorker
            implements SerialPortEventListener , Runnable , LayoutTimeoutManager.TimeoutAction
    {
        public CmriSerialPollingWorker()
        {
            m_in_mesg = new SynchronizedByteBuffer( 4000 * 2 );
            m_timeout_mgr = LayoutTimeoutManager.getInstance();
        }

        //--------------------  SerialPortEventListener  ----------------------

        /***
         *  Handle rxtx serial events. Dispatches the event to event-specific methods.
         *
         * <p> See: http://www.codeproject.com/Questions/450480/How-communicate-with-serial-port-in-Java
         *
         *  @param event The serial event
         */
        @Override
        public void serialEvent(SerialPortEvent event)
        {

            switch( event.getEventType() )
            {
                case SerialPortEvent.DATA_AVAILABLE :
                    try
                    {
                        while( m_instr.available() > 0 )
                        {
                            m_in_mesg.add( (byte) m_instr.read() );
                        }
                    }
                    catch( IOException ex )
                    {
                        LOG.log( Level.INFO, "CmriSerialPollingWorker#serialEvent() found EOF." );
                        m_in_mesg.add( CH_ERROR_BYTE );
                    }

                    break;

                // "OUTPUT_BUFFER_EMPTY is an optional event type.  Well hidden in the
                // documentation Sun states that not all JavaComm implementations
                // support generating events of this type."
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY :
                    break;

                case SerialPortEvent.FE :
                case SerialPortEvent.OE :
                case SerialPortEvent.PE :
                    // reception trouble....
                    m_in_mesg.add( CH_ERROR_BYTE );

                    m_cntr_bad_bytes_in += 1;
                    break;

                default :
                    // modem change, ring indicator, etc...  Ignored for CMRI.
                    break;
            }

        }

        //----------------------  interface Runnable  -------------------------

        /***
         *  Run state machine to communicate with CMRI units, until told to stop or is interrupted.
         *
         * <p>
         *  See http://stackoverflow.com/questions/12916580/why-arent-my-threads-timing-out-when-they-fail?rq=1
         *  for good practices about using {@code Thread.currentThread().isInterrupted()}.
         */
        @Override
        public void run()
        {
            LOG.log( Level.INFO, "Thread #{0} starting on " + m_port.toString() + " ...",
                                    Long.toString( Thread.currentThread().getId() ) );

            m_in_mesg.setMatchFirstByte( CMRI_CH_STX );

            //
            // "Open the input Reader and output stream. The choice of a
            // Reader and Stream are arbitrary and need to be adapted to
            // the actual application. Typically one would use Streams in
            // both directions, since they allow for binary data transfer,
            // not only character data transfer.
            //
            // "Since the main operation when using a modem is to transfer data
            // unaltered, the communication with the modem should be handled via
            // InputStream/OutputStream, and not a Reader/Writer."
            //
            try
            {
                m_instr = m_port.getInputStream();
                m_outstr = m_port.getOutputStream();
            }
            catch( IOException ex )
            {
                throw new RuntimeException( "Cannot get inputStream or outputStream on COM-port, not expected.", ex );
            }

            try
            {
                m_port.addEventListener( this );
            }
            catch( TooManyListenersException ex )
            {
                throw new RuntimeException( "Listener already installed, not expected.", ex );
            }

            //  We're interested in receive-type errors:
            m_port.notifyOnFramingError( true );
            m_port.notifyOnOverrunError( true );
            m_port.notifyOnParityError( true );

            //
            // Enable the events we are interested in
            //
            m_port.notifyOnDataAvailable(true);
            m_port.notifyOnOutputEmpty(true);

            try
            {
                m_port.setFlowControlMode( SerialPort.FLOWCONTROL_NONE );
            }
            catch( UnsupportedCommOperationException ex )
            {

            }

            //  With accumulation, once level reaches 1.0, then try one revival.
            float   recovering = 0.0f;

            //  We're open for business!
            m_port.setDTR( true );
            m_port.setRTS( true );

            try
            {
                while( ! m_do_thread_exit  )
                {
                    Thread.sleep( PROP_INTER_UNIT_SILENCE );
                    if( ! m_OK_to_poll_units )
                        continue;

                    //  1.  Check for reviving units.

                    if( recovering <= 1.0f && m_revive_queue.size() > 0 )
                    {
                        recovering += m_recovery_rate;
                        if( recovering >= 1.0f )
                        {
                            int  addr = m_revive_queue.remove();

                            if( recoverUnit( addr ) )
                            {
                                //  Yup, got it going.  Move to active queue.
                                m_active_queue.add( addr );
                                Thread.sleep( PROP_INTER_UNIT_SILENCE );
                            }
                            else
                            {
                                //  Sadness, no response.  Put on end of non-responding queue
                                //  for another query in a little bit.
                                m_revive_queue.add( addr );
                            }

                            //  We tried one.  Reset accumulator, which gives duration between attempts.
                            recovering -= 1.0f;
                        }
                    }

                    //  2.  Poll entire active list.

                    int  len = m_active_queue.size();
                    while( --len >= 0 && ! m_do_thread_exit )
                    {
                        int  addr = m_active_queue.remove();

                        if( queryResponseUnit( addr ) )
                        {
                            //  Still communicating, keep it around.
                            m_active_queue.add( addr );

                            // Slight pause between units of the poll cycle.
                            Thread.sleep( PROP_INTER_UNIT_SILENCE );
                        }
                        else
                        {
                            //  Sadness, it went silent on us.
                            m_revive_queue.add( addr );
                            Thread.yield();
                        }

                    }   // while active units to deal with..

                }   // while running...
            }
            catch( InterruptedException ex )
            {
                //  we are done, OK to exit thread.
                LOG.log( Level.INFO, "worker thread done", ex );
            }
            finally
            {
                LOG.log( Level.INFO, "Thread #{0} exiting...", Long.toString( Thread.currentThread().getId() ) );
            }
        }

        /***
         *  Attempt to recover a unit, which for CMRI means programming in its configuration
         *  and trying to get a response of some sort.
         *  Sometimes there is no way to validate the existence of a unit, e.g. it has no inputs to query.
         *  In that case, we assume the unit is functioning.
         *
         * @param addr unit's poll address, typically 0 to 127 , but not range checked.
         * @return true if unit communicated back, else false when no positive response.
         */
        protected boolean recoverUnit( int addr )
        {
            ArrayList<byte[]>  inits = m_model.getUnitInitializationStrings( addr );

            if( inits == null )
            {
                LOG.log( Level.WARNING, "Want to revive unit #{0} but have no init-message.", addr );
                return false;
            }

            try
            {
                drainReceivePort();

                for( byte[] m : inits )
                {
                    //  There is no response for CMRI INIT message, so use sendCmriMessage()
                    //  method directly.
                    sendCmriMessage( addr, m );
                }
            }
            catch( IOException ex )
            {
                throw new RuntimeException( "COM-port fail during recoverUnit().", ex );
            }

            return false;
        }

        /****
         *  Query a unit for changed inputs; if unit has no inputs then CMRI has no "idling response" so
         *  just assume the unit is functioning.
         *
         * @param addr unit's poll address, typically 0 to 127 , but not range checked.
         * @return true if unit communicated back or none expected, else false when no positive response.
         */
        protected boolean queryResponseUnit( int addr )
                throws InterruptedException
        {
            Future  timeout_future = null;

            try
            {
                m_in_mesg.setEnabled( true );
                int  chars_sent = sendCmriMessage( addr, null );

                //  Await first two bytes back from unit, which must match STX and unit's poll address
                //  Compute timeou assuming whole TX packet has been buffered by OS, so must wait
                //  for it to be fully sent, then a pause while th eunit interprets it, then
                //  time for 4 bytes to be sent back: FF, FF, STX, "addr"
                if( m_in_mesg.awaitCountAtLeast( 2, getTxToFirstRxTimeout(chars_sent + 2) ) )
                {
                    // Timeout waiting for first byte of response.
                    LOG.log( Level.FINE, "Timeout waiting for STX + poll-address response." );
                    _missedPoll( addr );
                    return false;
                }

                m_rx_timeout = false;
                timeout_future = this.m_timeout_mgr.timeoutMillis( getTxToFirstRxTimeout(chars_sent), this, null );

                // TODO: we got STX + UA back from unit.  Must probe the message to determine
                //      its length.  Might have to actually parse as we go for fastest response.

                try
                {
                    synchronized( timeout_future )
                    {
                        timeout_future.wait();
                    }
                }
                catch( InterruptedException ex )
                {
                    // Oh dear, timeout before whole packet received.
                }

                m_consecutive_missed_polls[ addr ] = 0;
                return true;
            }
            catch( IOException ex )
            {
            }
            finally
            {
                m_in_mesg.setEnabled( false );

                if( timeout_future != null && ! timeout_future.isDone() )
                    timeout_future.cancel( true );
            }

            _missedPoll( addr );
            return false;
        }

        @Override
        public void onTimeout( Object anchor )
        {
            m_rx_timeout = true;
        }

        //----------------------  MESSAGES METHODS  ------------------------

        /***
         *  Send out a CMRI message to some unit; framing, checksum generation and escaping are done here.
         *
         * @param addr unit poll address
         * @param mesg bytes of message, at least 1 byte which holds the command byte.
         *
         * @return Number of bytes sent out.
         * @throws IOException for general output issues.
         * @throws IllegalArgumentException if {@link mesg} is null or has zero size.
         */
        private int sendCmriMessage( int addr, byte[] mesg )
                throws IOException, IllegalArgumentException
        {

            if( mesg == null || mesg.length < 1 )
                throw new IllegalArgumentException( "CMRI message must be at least 1 byte: the command.");

            m_outstr.write( CMRI_HEADER_BYTES );

            //  UA is sent as an ASCII letter on wire.
            m_outstr.write( (byte) (addr + CMRI_ADDR_OFFSET) );

            int     cntr_escapes = 0;

            //  Ecape any bytes as they are sent.  UA is not escaped.
            for( byte b : mesg )
            {
                if( b == CMRI_CH_STX || b == CMRI_CH_ETX || b == CMRI_CH_ESCAPE )
                {
                    cntr_escapes++;
                    m_outstr.write(CMRI_CH_ESCAPE );
                }
                m_outstr.write( b );
            }

            //  Drain any input just before sending ETX.  This ensures we're ready to RX!
            drainReceivePort();

            m_outstr.write( CMRI_TRAILER_BYTES );

            int  count = CMRI_HEADER_BYTES.length + 1 + mesg.length + cntr_escapes + CMRI_TRAILER_BYTES.length;
            m_cntr_good_bytes_out += count;

            return( count );
        }

        private void receiveMessage( int bytesSent )
        {

        }

        /***
         *  Make any pending RX bytes go away, but does so at at most a few seconds.
         * @throws IOException when stream has been closed.
         */
        private void drainReceivePort()
                throws IOException
        {
            // wait at most 'PROP_DRAIN_PORT_MAX_WAIT_SECONDS' seconds, in 5 micro-second units.
            int  max_waits = (int) (200 * 1000 * PROP_DRAIN_PORT_MAX_WAIT_SECONDS);

            while( --max_waits >= 0 && m_instr.available() > 0 )
            {
                //  do a short sleep first.
                try {
                    Thread.sleep( 0, 5 * 1000 );    // sleep 5 microseconds, in nanos.
                }
                catch( InterruptedException ex )
                {
                    Thread.currentThread().interrupt();
                    return ;
                }

                m_instr.read();
            }
            m_in_mesg.clear();
        }

        //--------------------------  HELPER METHODS  -------------------------

        /***
         *  Sleep a short time, in milliseconds, with quick return if interrupted which causes
         *  the worker thread to exit.
         *  Positive value does a sleep ; zero will yield the thread's time slice.
         *
         * <p> If the thread is interrupted, then that exception is re-thrown.
         *
         * @param milliseconds milliseconds of sleep, or 0 to just give up thread's time slice.
         */
        public void shortSleep( int milliseconds )
        {
            if( m_do_thread_exit )
            {
                //  Time to exit thread, signal interrupt to start the duty.
                Thread.currentThread().interrupt();
                return ;
            }

            if( milliseconds > 0 )
            {
                try { Thread.sleep( milliseconds ); }
                catch( InterruptedException ex )
                {
                    //  re-signal, so maybe thread will exit.
                    Thread.currentThread().interrupt();
                }
            }
            else
            if( milliseconds == 0 )
            {
                Thread.yield();
            }
        }

        //-------------------------  CONTROL METHODS  -------------------------

        /***
         *  Turn on or off polling.
         *  When off, then worker thread will sleep with periodic checks back here.
         *  Stop can be either after done with unit, or between poll cycles.
         *
         * @param ok true to start polling
         */
        public void setOkToPoll( boolean ok )
        {
            m_OK_to_poll_units = ok;
        }

        /***
         *  Ask the worker thread to exit.
         *  Afterwards use {@code thread.join()} for cleanup.
         */
        public void stopWorkerThread()
        {
            m_do_thread_exit = true;
        }

        //-------------------------  INSTANCE VARS  ---------------------------

        protected volatile boolean      m_OK_to_poll_units = false;

        protected volatile boolean      m_do_thread_exit = false;

        protected InputStream           m_instr;

        protected OutputStream          m_outstr;

        /*** Offset added to unit address before sending on the wire. */
        public final byte   CMRI_ADDR_OFFSET = (byte) 0x41;

        public final byte   CMRI_CH_ESCAPE   = (byte) 0x10;
        public final byte   CMRI_CH_STX      = (byte) 0x02;    // start byte
        public final byte   CMRI_CH_ETX      = (byte) 0x03;    // start byte
        public final byte   CMRI_CH_FRAME    = (byte) 0xff;

        /*** Indicates an RX error occurred and is now cleared. */
        public final byte   CH_ERROR_BYTE    = (byte) CMRI_CH_FRAME;

        public final byte[] CMRI_HEADER_BYTES = new byte[] { CMRI_CH_FRAME, CMRI_CH_FRAME, CMRI_CH_STX };
        public final byte[] CMRI_TRAILER_BYTES = new byte[] { CMRI_CH_ETX /*, CMRI_CH_FRAME */ };

        /***
         *  Where current in-message is collected.  If disabled, then no message
         *  is expected.
         */
        private final SynchronizedByteBuffer   m_in_mesg;

        private final LayoutTimeoutManager    m_timeout_mgr;

        private boolean                 m_rx_timeout;

    }


    /***
     *   Milliseconds of quiet time between unit poll-response.
     *   If we wait for a non-responding unit, that wait time is considered part of
     *   the inter-unit poll-response quiet time.
     */
    public int          PROP_INTER_UNIT_SILENCE = 30;

    /***
     *  Max time to wait when draining the RX port.  Prevents live-lock if a slave unit
     *  is babbling.  Value is {@code double}, so max ease of fine tuning...
     */
    public double       PROP_DRAIN_PORT_MAX_WAIT_SECONDS = 0.5;

    /***
     *  After missing this many polls, send an INIT message to unit to try and re-enroll.
     *  In case it lost its configuration, e.g. the unit was power-cycled.
     */
    public int          PROP_GONE_TOO_LONG_REINIT = 5;

    /*  Smallest logical polling address. */
    public final int    CMRI_LOWEST_POLL_ADDR   = 0;

    /*  Maximum logical polling address. */
    public final int    CMRI_HIGHEST_POLL_ADDR  = 255;

    //---------------------------  INSTANCE VARS  -----------------------------

    public volatile long            m_cntr_bad_bytes_in;

    public volatile long            m_cntr_good_bytes_in;

    public volatile long            m_cntr_good_bytes_out;

    /*** Model we feed sensor changes into. */
    protected final CmriLayoutModelImpl   m_model;

    private boolean             m_polling;

    /***  Fractional addition until reaching 1.0, then a revival will occur. */
    protected volatile float    m_recovery_rate;

    /*** Time to wait for a first char of response before giving up, in milliseconds. */
    protected int               m_response_wait;

    /*** Synchronized queue of units actively responding. */
    protected Queue<Integer>    m_active_queue;

    /*** Synchronized queue of known units that are NOT responding. */
    protected Queue<Integer>    m_revive_queue;

    /***
     *  Counter of missed polls for some unit poll address, reset to zero when
     *  a poll-response is succcessful.
     */
    transient private int[]             m_consecutive_missed_polls;

    transient protected SerialPort      m_port;

    transient protected int             m_baud_rate;

    transient protected CmriSerialPollingWorker   m_worker;

    transient protected Thread          m_thread;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriPollMachine.class.getName() );

}
