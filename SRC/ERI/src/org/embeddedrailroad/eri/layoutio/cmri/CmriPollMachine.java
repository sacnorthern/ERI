/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Queues;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/***
 *   Provides a state-machine to poll units on a CMRI bank via some comms-channel.
 *   Model provides initialize strings for units, and a place to dump input-data updates.
 *   Instance has a little worker-thread running in a local-class-instance.
 * <p>
 *   Almost all the public methods are {@code synchronized}.  Happily, these
 *   methods can call other {@code synchronized} methods of the same class.
 *   That said, please don't use instances for locking in a client class.
 *   The {@code synchronized} methods already to that!
 *
 * @author brian
 */
public class CmriPollMachine
            implements UncaughtExceptionHandler
{
    public CmriPollMachine( SerialPort serial, CmriLayoutModelImpl model )
    {
        this.m_model = model;
        this.m_recovery_rate = 0.5f;
        this.m_port = serial;

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
    public void setTimeout( int millisecs )
    {
        m_response_wait = millisecs;
    }

    public int getTimeout()
    {
        return this.m_response_wait;
    }

    /***
     *  Set recovery rate to re-try units that are not responding.
     *  After every poll cycle, the recovery rate is added to an accumulator.
     *  Once 1.0 is reached ( or exceeded ), communication with a unit is attempted.
     *
     * @param rr value more than 0.0 but not above 1.0.
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
                    m_thread.setName( m_worker.getClass().getSimpleName() );
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
                m_worker.stopSelf();
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
     *  Catch before falling our worker thread.
     *  Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
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
     *  See http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
     */
    class CmriSerialPollingWorker
            implements SerialPortEventListener , Runnable
    {
        //--------------------  SerialPortEventListener  ----------------------

        /***
         *  Handle serial events. Dispatches the event to event-specific methods.
         *  @param event The serial event
         */
        @Override
        public void serialEvent(SerialPortEvent event)
        {

        }

        //----------------------  interface Runnable  -------------------------

        @Override
        public void run()
        {
            LOG.log( Level.INFO, "Thread #{0} starting...", Long.toString( Thread.currentThread().getId() ) );

            //
            // Open the input Reader and output stream. The choice of a
            // Reader and Stream are arbitrary and need to be adapted to
            // the actual application. Typically one would use Streams in
            // both directions, since they allow for binary data transfer,
            // not only character data transfer.
            //
            try
            {
                m_instr = m_port.getInputStream();
                m_outstr = m_port.getOutputStream();
            }
            catch( IOException ex )
            {
                throw new RuntimeException( "Cannot get inputStream or outputStream on COM-port.", ex );
            }

            //  With accumulation, once level reaches 1.0, then try one revive.
            float   recovering = 0.0f;

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
                            recovering = 0.0f;
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
                            Thread.sleep( PROP_INTER_UNIT_SILENCE );
                        }
                        else
                        {
                            //  Sadness, it went silent on us.
                            m_revive_queue.add( addr );
                        }
                    }   // while active units to deal with..

                }   // while running...
            }
            catch( InterruptedException ex )
            {
                //  we are done, OK to finish.
                LOG.log( Level.INFO, "worker thread done", ex );
            }

            LOG.log( Level.INFO, "Thread #{0} exiting...", Long.toString( Thread.currentThread().getId() ) );
        }

        /***
         *  Attempt to recover a unit, which for CMRI means programming in its configuration
         *  and trying to get a response of some sort.
         *  Sometimes there is no way to validate the existence of a unit, e.g. it has no inputs to query.
         *  In that case, we assume the unit is functioning.
         *
         * @param addr unit's poll address, typically 0 to 63 , but not range checked.
         * @return true if unit communicated back, else false when no positive response.
         */
        protected boolean recoverUnit( int addr )
        {
            ArrayList<byte[]>  inits = m_model.getUnitInitializationStrings( addr );

            if( inits == null )
            {
                LOG.log( Level.WARNING, "Want to revive unit {0} but have no init-message", addr );
                return false;
            }

            try
            {
                drainInPort();

                for( byte[] m : inits )
                {
                    sendMessage( addr, m );
                }
            }
            catch( IOException ex )
            {
                throw new RuntimeException( "COM-port fail during recoverUnit().", ex );
            }

            return false;
        }

        /****
         *  Query a unit for changed inputs; if no inputs then CMRI has no "idling response" so
         *  just assume the unit is functioning.
         *
         * @param addr unit's poll address, typically 0 to 63 , but not range checked.
         * @return true if unit communicated back, else false when no positive response.
         */
        protected boolean queryResponseUnit( int addr )
        {
            return true;
        }

        //-------------------------  MESSAGES METHODS  ------------------------

        protected void sendMessage( int addr, byte[] mesg )
                throws IOException
        {
            m_outstr.write( CMRI_HEADER_BYTES );

            m_outstr.write( CMRI_TRAILER_BYTES );
        }

        protected void receiveMessage( )
        {

        }

        protected void drainInPort()
                throws IOException
        {
            while( m_instr.available() > 0 )
                m_instr.read();
        }

        //--------------------------  HELPER METHODS  -------------------------

        /***
         *  Sleep a short time, in milliseconds, with quick return if interrupted.
         * @param milliseconds milliseconds of sleep.
         */
        public void shortSleep( int milliseconds )
        {
            if( milliseconds >= 0 )
            {
                try { Thread.sleep( milliseconds ); }
                catch( InterruptedException ex ) { }
            }
        }

        //-------------------------  CONTROL METHODS  -------------------------

        /***
         *  Turn on or off polling.  When off, then worker thread will sleep.
         * @param ok
         */
        public void setOkToPoll( boolean ok )
        {
            m_OK_to_poll_units = ok;
        }

        /***
         *  have the worker thread exit, after which the thread exits.
         */
        public void stopSelf()
        {
            m_do_thread_exit = true;
        }

        //-------------------------  INSTANCE VARS  ---------------------------

        protected volatile boolean      m_OK_to_poll_units = false;

        protected volatile boolean      m_do_thread_exit = false;

        protected InputStream           m_instr;

        protected OutputStream          m_outstr;

        public final byte   CMRI_ESCAPE      = (byte) 0x1f;
        public final byte   CMRI_ADDR_OFFSET = (byte) 0x40;

        public final byte[] CMRI_HEADER_BYTES = new byte[] { (byte) 0xff, (byte) 0xff };
        public final byte[] CMRI_TRAILER_BYTES = new byte[] { (byte) 0xff };

    }   // end class CmriSerialPollingWorker ..

    //----------------------  CONFIGURATION CONSTANTS  ------------------------

    /***
     *   Milliseconds of quiet time between unit poll-response.
     *   If we wait for a non-responding unit, that wait time is considered part of
     *   the inter-unit poll-response quiet time.
     */
    public int          PROP_INTER_UNIT_SILENCE = 30;

    //---------------------------  INSTANCE VARS  -----------------------------

    protected final CmriLayoutModelImpl   m_model;

    private boolean             m_polling;

    /***  Fractional addition until reaching 1.0, then a revival will occur. */
    protected float             m_recovery_rate;

    /*** Time to wait for a first char of response before giving up, in milliseconds. */
    protected int               m_response_wait;

    /*** Synchronized queue of units actively responding. */
    protected Queue<Integer>    m_active_queue;

    /*** Synchronized queue of known units that are NOT responding. */
    protected Queue<Integer>    m_revive_queue;

    transient protected SerialPort      m_port;

    transient protected CmriSerialPollingWorker   m_worker;

    transient protected Thread          m_thread;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriPollMachine.class.getName() );

}
