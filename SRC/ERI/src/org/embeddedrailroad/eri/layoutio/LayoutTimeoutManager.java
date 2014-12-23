/***  Java-ERI    Java-based Embedded Railroad Interfacing.
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
 ***
 ***  Licensed under the Apache License, Version 2.0 ( the "License" ) ;
 ***  you may not use this file except in compliance with the License.
 ***  You may obtain a copy of the License at:
 ***        http://www.apache.org/licenses/LICENSE-2.0
 ***
 ***  Unless required by applicable law or agreed to in writing, software
 ***  distributed under the License is distributed on an "AS IS" BASIS,
 ***  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.layoutio;

import java.util.concurrent.*;

/**
 *  Singleton to manage timeouts for objects in the Layout IO system.
 *
 * <p> Although you can make multiple objects, there is just a single {@link ThreadPoolExecutor} object
 *  shared among all.
 *  What is really important are the {@link Future} objects returned from the
 *  {@link #timeoutMillis(int, org.embeddedrailroad.eri.layoutio.LayoutTimeoutManager.TimeoutAction, java.lang.Object) }
 *  methods.
 *  These objects are the <em>real</em> objects of interest.
 *  In a sense, the {@code timeoutMillis()} methods are factory methods on a "wrapped" static
 *  {@link ThreadPoolExecutor} object.
 *
 * <br><ul><li> see https://groups.google.com/forum/#!msg/comp.lang.java.programmer/JoyZLGZu2m4/f3oR8NWd6g8J
 * <li> see http://stackoverflow.com/questions/2142287/resettable-timeout-in-java?rq=1
 * <li> see http://tutorials.jenkov.com/java-util-concurrent/scheduledexecutorservice.html
 * <li> see http://en.wikipedia.org/wiki/Futures_and_promises
 * </ul>
 *
 * @author brian
 */
public class LayoutTimeoutManager
{

    private LayoutTimeoutManager()
    {
        //
        //  The max-thread-count param does not affect how many timeouts can be pending.
        //  Rather it limits the number of concurrent timeouts than can be expired simultaneously.
        //  If more than "max" expire than the first runs execute immediately.
        //  The rest queue up waiting for any of the first ones to complete.
        //  Completing means returning from the callback in TimeoutAction#timeoutAction().
        //
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
     *  Stop the timeout management immediately, cancelling all pending timeout requests.
     *  From now until the application exits, submitting a timeout request is likely to fail....
     */
    public void shutdownNow()
    {
        m_executor.shutdownNow();
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

    /***
     *  Sets up a timeout that invokes a callback when time expires, or does nothing if cancelled.
     *
     * <p> If you want to transform some object and return something different based on it.
     * The new
     *
     * <pre>        public Object onTimeout( Object anchor )
        {
            System.out.println( "  onTimeout()  " );
            int  v = (Integer) anchor;
            return new Integer( v + 1 );
        }
    </pre>
     *  And then in your client code, you can retrieve the value-object with a call to {@code future.get()}.
     *  It blocks until the value has been produced.
     *
     * @param milliseconds how long to wait, in milliseconds.
     * @param notifyObject Client object to callback when timeout expires
     * @param anchor Parameter to callback
     * @return {@link Future} so timeout can be cancelled, or tested if it ran to completion.
     */
    public Future timeoutMillis( int milliseconds, final TimeoutValueAction notifyObject, final Object anchor )
    {
        ScheduledFuture  scheduledFuture;

        //  A 'Runnable' canont throw exceptions and cannot return a value.
        //  A 'Callable' can do both of these, but that is overkill for just a timeout.
        scheduledFuture = m_executor.schedule( new Callable<Object>()
                {
                    @Override
                    public Object call() throws Exception
                    {
                        return notifyObject.onTimeout( anchor );
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

    //-------------------------------  CALLBACK INTERFACE  ------------------------------

    /***
     *  Interface for client's object that is called when timeout expires, but not called
     *  if timeout is cancelled.
     *  Method {@link #onTimeout(java.lang.Object) } cannot throw exceptions.
     */
    public interface TimeoutValueAction
    {
        /***
         *  Call back when timeout has expired without first being cancelled.
         *
         * @param anchor object-ref from when timeout was set up.
         * @return Some object the client would like to use.
         * @throws java.lang.Exception cuz you can...
         */
        Object onTimeout( Object anchor ) throws Exception;

    }   // end public interface TimeoutValueAction ..

    //--------------------------  Deamon Thread Pool Executor  --------------------------

    /***
     *  Derived class from {@link ScheduledThreadPoolExecutor} so application here has
     *  a handle-wrapper that can be extended if the need arises.
     */
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
