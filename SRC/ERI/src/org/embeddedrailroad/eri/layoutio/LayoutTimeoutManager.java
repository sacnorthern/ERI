/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio;

import java.util.concurrent.*;

/**
 *  Singleton to manage timeouts for objects in the Layout IO system.
 *
 * <p> see https://groups.google.com/forum/#!msg/comp.lang.java.programmer/JoyZLGZu2m4/f3oR8NWd6g8J
 * <br> see http://stackoverflow.com/questions/2142287/resettable-timeout-in-java?rq=1
 * <br> see http://tutorials.jenkov.com/java-util-concurrent/scheduledexecutorservice.html
 *
 * @author brian
 */
public class LayoutTimeoutManager
{

    private LayoutTimeoutManager()
    {
        m_executor = new DaemonThreadPool( 9 );
    }

    /***
     *  Get access to singleton timeout manager.
     * @return The Timeout Manager.
     */
    public static LayoutTimeoutManager  getInstance()
    {
        if( s_instance == null )
        {
            synchronized (LayoutTimeoutManager.class) {
                if (s_instance == null) {  // second time lock
                    s_instance = new LayoutTimeoutManager();
                }
            }
        }
        return( s_instance );
    }

    /***
     *  Setup a timeout that invokes a callback when time expires, or does nothing if cancelled.
     *
     * <p> If you need to communicate a change in a low-overhead way, i.e. not create a class
     *  to hold a value, consider using an array.
     *  An array gives you pass-by-reference semantics.
     *  Here is code for a typical callback, where {@code anchor} is <tt>Integer[1]</tt> array.
     *  Note the use of <tt>synchronized</tt> internally to ensure the access and update are coordinated.
     *
     * <pre>        public void onTimeout( Object anchor )
        {
            System.out.println( "  onTimeout()  " );
            synchronized( anchor )
            {
                Integer[]  arry = (Integer[]) anchor;
                arry[ 0 ] += 139;
            }
        }
    </pre>
     *
     * @param milliseconds how long to wait, in milliseconds.
     * @param notifyObject Client object to callback when timeout expires
     * @param anchor Parameter to callback
     * @return {@link Future} so timeout can be cancelled, or tested if it ran to completion.
     */
    public Future timeoutMillis( int milliseconds, final TimeoutAction notifyObject, final Object anchor )
    {
        ScheduledFuture  scheduledFuture;

        //  A 'Runnable' canont throw exceptions and cannot return a value.
        //  A 'Callable' can do both of these, but that is overkill for just a timeout.
        scheduledFuture = m_executor.schedule( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        notifyObject.onTimeout( anchor );
                    }
                },
                milliseconds,
                TimeUnit.MILLISECONDS );

        return scheduledFuture;
    }

    //-------------------------------  INSTANCE VARIABLES  ------------------------------

    /*** Singleton for manager. */
    private static volatile LayoutTimeoutManager    s_instance;

    /*** Schedules the timeout callback invocations. */
    protected ScheduledExecutorService    m_executor;

    //-------------------------------  CALLBACK INTERFACE  ------------------------------

    /***
     *  Interface for client's object that is called when timeout expires, but not called
     *  if timeout is cancelled.
     *  Method {@link #onTimeout(java.lang.Object) } cannot throw exceptions.
     */
    public interface TimeoutAction
    {
        /***
         *  Call back when timeout has expired without first being cancelled.
         * @param anchor object-ref from when timeout was set up.
         */
        void onTimeout( Object anchor );

    }   // end public interface TimeoutAction ..

    //--------------------------  Deamon Thread Pool Executor  --------------------------

    protected class DaemonThreadPool extends ScheduledThreadPoolExecutor
    {

        public DaemonThreadPool( int corePoolSize ) {
            super( corePoolSize );
        }

        @Override
        protected void beforeExecute(Thread t,
                 Runnable r)
        {
            // "If not otherwise specified, a Executors.defaultThreadFactory() is used,
            //  that creates threads to all be in the same ThreadGroup and with the same
            //  NORM_PRIORITY priority and non-daemon status."
            //
            //  Change to daemon, even though thread runs for just a moment.
            //
            //  And when I did that, I got this error message:
            //
            //Exception in thread "pool-1-thread-1" java.lang.IllegalThreadStateException
            //	at java.lang.Thread.setDaemon(Thread.java:1365)
            //	at org.embeddedrailroad.eri.layoutio.LayoutTimeoutManager$DaemonThreadPool.beforeExecute(LayoutTimeoutManager.java:117)
            //	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
            //	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
            //	at java.lang.Thread.run(Thread.java:722)

            // t.setDaemon( true );
            super.beforeExecute( t, r );
        }

    }   // end protected class DaemonThreadPool ..

}
