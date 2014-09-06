/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import com.google.common.collect.Queues;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 *   Provides a state-machine to poll units on a CMRI bank via some comms-channel.
 *   Instance has a little worker-thread running in a local-class-instance.
 *
 * @author brian
 */
public class CmriPollMachine
            implements UncaughtExceptionHandler
{
    public CmriPollMachine( SerialPort serial )
    {
        this.m_recovery_rate = 0.5f;
        this.m_port = serial;
        this.m_active_queue = Queues.synchronizedQueue( new java.util.LinkedList<Integer>() );
        this.m_revive_queue = Queues.synchronizedQueue( new java.util.LinkedList<Integer>() );
    }

    //----------------------------  BEAN THINGS  ------------------------------

    public boolean isPolling()
    {
        return this.m_polling;
    }

    public void setTimeout( int millisecs )
    {
        m_response_wait = millisecs;
    }

    public int getTimeout()
    {
        return this.m_response_wait;
    }

    public void setRecoveryRate( float rr )
    {
        if( rr <= 0.0f || rr > 1.0f )
            throw new IllegalArgumentException( "recovery rate must be (0.0 .. 1.0]" );

        this.m_recovery_rate = rr;
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
    public void addUnitToPollingList( int unitAddr )
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
    public void removeUnitFromPollingList( int unitAddr )
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
    public Set<Integer>  getKnownUnits( boolean includeNonResponding )
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

    /***
     *  Start or stop polling, on first start creates the worker thread.
     *  Creation is automatic; however, for worker-thread exit-join, the client must call {@link #shutdown()}
     *  explicitly.
     *
     * @param goPoll true to start, false to stop.
     */
    public void setPolling( boolean goPoll )
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
     *  Stop any worker thread from polling and then do {@link Thread.join()} to reclaim it.
     *  Will wait just a few seconds for worker-thread to exit itself before returning to caller.
     */
    public synchronized void shutdown()
    {
        //  Stop it, just in case...
        if( m_polling == true )
        {
            setPolling( false );
        }

        if( m_thread != null )
        {
            try {
                m_worker.stopSelf();
                m_thread.join( 5000 );      // 5000 = 5 seconds.
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

            // See http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

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

    }   // end class CmriSerialPollingWorker ..

    //----------------------  CONFIGURATION CONSTANTS  ------------------------

    /***
     *   Milliseconds of quiet time between unit poll-response.
     *   If we wait for a non-responding unit, that wait time is considered part of
     *   the inter-unit poll-response quiet time.
     */
    public int          PROP_INTER_UNIT_SILENCE = 30;

    //---------------------------  INSTANCE VARS  -----------------------------

    transient boolean           m_polling;

    /***  Fractional addition until reaching 1.0, at then a revival will occur. */
    transient float             m_recovery_rate;

    /*** Time to wait for a first char of response before giving up, in milliseconds. */
    transient int               m_response_wait;

    /*** Synchronized queue of units actively responding. */
    transient Queue<Integer>    m_active_queue;

    /*** Synchronized queue of known units that are NOT responding. */
    transient Queue<Integer>    m_revive_queue;

    transient protected SerialPort  m_port;

    transient CmriSerialPollingWorker   m_worker;

    transient protected Thread      m_thread;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriPollMachine.class.getName() );

}
