/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  A very simple buffer that does NOT wrap but can be enabled or disabled (to block new
 *  {@link SynchronizedByteQueue#add(byte) } calls.
 *  The buffer that is filled one {@code byte} at a time , but returns a byte-array
 *  when requested to give up its content.
 *  If first-byte matching is enabled, then initial bytes that do not match are ignored.
 *
 * <p> Since Java doesn't permit primitive types for type-parameters,
 *  "byte" is part of class name.
 *
 * @author brian
 */
public class SynchronizedByteBuffer
{

    public SynchronizedByteBuffer( int maxSize )
    {
        m_count = new AtomicInteger();
        m_enabled = new AtomicBoolean( false );
        m_array = new byte[ maxSize ];
        m_match_first_byte = MATCH_NONE;
    }

    //-----------------------  Buffer Data Management  ------------------------

    /***
     *  Resets buffer so it is holding zero bytes.
     *  Notifies any threads awaiting change in 'count'.
     *  Does not change enabled/disabled state.
     */
    public void     clear()
    {
        synchronized( m_count )
        {
            m_count.set( 0 );
            m_count.notifyAll();
        }
    }

    /***
     *  Appends {@code ch} to end of buffer, incrementing count, and notifying any
     *  threads waiting for a change to 'count'.
     *  Only stores when the buffer is 'enabled'.
     *  If match-first-char is enabled, such byte must be received before storing is enabled.
     *
     * <p> Throws illegal-array-index runtime error if buffer storage overflows.
     *  If {@code ch} successfully added, notifies all threads waiting for a change of 'count'.
     *
     * @param ch byte to append to buffer.
     */
    public void     add( byte ch )
    {
        if( m_enabled.get() )
        {
            synchronized( m_count )
            {
                int  i = m_count.get();

                /* "If could store first byte AND matching enabled AND byte isn't the match-firstt char, then skip..." */
                if( i == 0 && m_match_first_byte != MATCH_NONE && ch != m_match_first_byte )
                {
                    // await STX char...
                }
                else
                {
                    m_count.incrementAndGet();
                    m_array[ i ] = ch;
                    m_count.notifyAll();
                }
            }
        }
        //--- DEBUG vvv
        else {
            System.out.printf( "#add(%02x) while disabled, ignored.\n", ch );
        }
        //--- DEBUG ^^^
    }

    /***
     *  Disable the accumulation of data and return an array of bytes {@link #add(byte) }'ed.
     *  If no bytes have been add()'ed, then a zero-length byte array is returned.
     *  Caller owns the returned byte-array.
     *
     * <p> Notifies all threads waiting on change of 'count' since afterwards it will be zero.
     *
     * @return byte-array of correct length, never {@code null}.
     */
    public byte[]   disableAndGet()
    {
        synchronized( m_count )
        {
            m_enabled.set( false );
            byte[]  bunch = Arrays.copyOf( m_array, m_count.getAndSet( 0 ) );
            m_count.notifyAll();

            return( bunch );
        }
    }

    //---------------------------  Concurrent Synchronization  ---------------------------

    /***
     *  Waits the caller until either a minimum number of chars received, or a timeout is reached.
     *  If {@code timeoutMilliseconds} is zero, then no waiting occurs.
     *
     * <p> Uses {@link ManagementFactory#getRuntimeMXBean() } so time is monotonically increasing.
     *  Using {@link System#currentTimeMillis() }, then time can skip forward, like for daylight savings time.
     *  In Java, the granularity of timers is not specified.
     *
     * <p> See http://stackoverflow.com/questions/817801/time-since-jvm-started
     *
     * @param minCountRequested minimum chars in buffer before returning.
     * @param timeoutMilliseconds maximum time to wait before returning, in milliseconds,
     *              or zero to just test without waiting.
     * @return false if sufficient chars received ; true if timeout occurred first.
     * @throws java.lang.InterruptedException When time to exit thread...
     */
    public boolean  awaitCountAtLeast( int minCountRequested, int timeoutMilliseconds )
            throws InterruptedException
    {
        final RuntimeMXBean  runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        long  remaining_millis = timeoutMilliseconds;
        long  next_start = runtimeMXBean.getUptime();

        //  We do some gymnastics with the timeout here.  m_count.wait(0) ::=
        //  "If timeout is zero, however, then real time is not taken into consideration
        //   and the thread simply waits until notified."
        //
        //  However, for this method "timeoutMilliseconds == 0" means non-block test.

        if( remaining_millis > 0 )
        {
            do
            {
                long  start_time = next_start;
                synchronized( m_count )
                {
                    m_count.wait(remaining_millis );
                }

                next_start = runtimeMXBean.getUptime();
                remaining_millis -= (next_start - start_time);
            } while( remaining_millis > 0 && getCount() < minCountRequested );
        }

        return ( getCount() < minCountRequested );
    }

    //-----------------------------  Bean Things  -----------------------------

    /***
     *  Returns count of bytes available, which is only stable if {@link #add(byte)}-ing has been disabled.
     * @return current count of bytes available.
     */
    public int     getCount()
    {
        synchronized( m_count )
        {
            return m_count.get();
        }
    }

    /***
     *  Returns whether or not accumulation is enabled.
     *  When disabled, no new bytes can be added to the buffer.
     * @return {@code true} if queuing is enabled.
     */
    public boolean  getEnabled()
    {
        synchronized( m_enabled )
        {
            return m_enabled.get();
        }
    }

    /***
     *  Change if accumulation of bytes is enabled or disabled.
     *  When disabled, no new bytes can be added to the buffer.
     *
     * @param en {@true} to permit queuing of incoming bytes.
     */
    public void    setEnabled( boolean en )
    {
        synchronized( m_enabled )
        {
            m_enabled.set( en );
        }
    }

    /***
     *  Returns first byte to match, or MATCH_NONE if feature disabled.
     * @return Byte to match, or MATCH_NONE if not matching.
     */
    public int      getMatchFirstByte()
    {
        return m_match_first_byte;
    }

    /***
     *  Sets the first byte stored, all other first bytes are ignored.
     * @param ch0 Byte to match on, or MATCH_NONE to turn off feature.
     */
    public void     setMatchFirstByte( int ch0 )
    {
        m_match_first_byte = ch0;
    }

    //-------------------------  Instance Variables  --------------------------

    private final AtomicBoolean     m_enabled;

    private final AtomicInteger     m_count;

    private final byte[]            m_array;

    private int                     m_match_first_byte;

    public final int                MATCH_NONE = -1;

}
