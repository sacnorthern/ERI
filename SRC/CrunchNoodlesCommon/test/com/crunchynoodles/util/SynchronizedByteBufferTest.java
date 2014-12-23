/***  This file is dedicated to the public domain, 2014 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brian
 */
public class SynchronizedByteBufferTest {

    public SynchronizedByteBufferTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println( "-- setUpClass() --" );
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println( "-- tearDownClass() --" );
    }

    /**
     * Test of clear method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testClear() {
        System.out.println( "-- clear --" );
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );
        instance.clear();

        int  cnt = instance.getCount();
        assertTrue( cnt == 0 );
    }

    /**
     * Test of add method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testAdd() {
        System.out.println( "-- add --" );
        byte ch = 1;
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );

        instance.add( ch++ );
        int  cnt = instance.getCount();
        assertTrue( cnt ==  1 );

        instance.add( ch++ );
        cnt = instance.getCount();
        assertTrue( cnt ==  2 );

        instance.add( ch++ );
        cnt = instance.getCount();
        assertTrue( cnt ==  3 );
    }

    /**
     * Test of disableAndGet method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testDisableAndGet() {
        System.out.println( "-- disableAndGet --" );
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );

        //  Put in some content before doing disable.
        byte  ch = 2;
        instance.add(  ch++ );
        instance.add(  ch++ );
        instance.add(  ch++ );

        byte[] expResult = { 2, 3, 4 };
        byte[] result = instance.disableAndGet();
        assertArrayEquals( expResult, result );
        assertFalse( instance.getEnabled() );

        //  buffer is disabled, so this add() will be ignored.
        //  Since we just drained the buffer, it will have no content
        instance.add( ch++ );
        assertEquals( instance.getCount(), 0 );

        result = instance.disableAndGet();
        expResult = new byte[] { };     // empty.
        assertArrayEquals( expResult, result );
        assertFalse( instance.getEnabled() );

    }

    /**
     * Test of awaitCountAtLeast method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testAwaitCountAtLeast()
            throws Exception {
        System.out.println( "-- awaitCountAtLeast --" );
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );

        //  "sufficient chars received" is 0
        int timeoutMilliseconds = 1000;
        boolean expResult = false;
        boolean result = instance.awaitCountAtLeast( 0, timeoutMilliseconds );
        assertEquals( expResult, result );

        instance.add( (byte) 33 );
        expResult = false;      // false means no timeout.
        result = instance.awaitCountAtLeast( 1, timeoutMilliseconds );
        assertEquals( expResult, result );

        expResult = true;      // true means a timeout.
        result = instance.awaitCountAtLeast( 5, timeoutMilliseconds );
        assertEquals( expResult, result );

    }

    /**
     * Test of getCount method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testGetCount() {
        System.out.println( "-- getCount --" );
        byte ch = 1;
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );
        assertTrue( instance.getEnabled() );

        instance.add( ch++ );
        int  cnt = instance.getCount();
        assertTrue( cnt ==  1 );

        instance.add( ch++ );
        cnt = instance.getCount();
        assertTrue( cnt ==  2 );

        instance.add( ch++ );
        cnt = instance.getCount();
        assertTrue( cnt ==  3 );
        assertTrue( instance.getEnabled() );
    }

    /**
     * Test of getEnabled method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testGetEnabled() {
        System.out.println( "-- getEnabled --" );
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        boolean expResult = false;
        boolean result = instance.getEnabled();
        assertEquals( expResult, result );

        expResult = true;
        instance.setEnabled( expResult );
        result = instance.getEnabled();
        assertEquals( expResult, result );
    }

    /**
     * Test of setEnabled method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testSetEnabled() {
        System.out.println( "-- setEnabled --" );
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        boolean expResult = false;
        boolean result = instance.getEnabled();
        assertEquals( expResult, result );

        expResult = true;
        instance.setEnabled( expResult );
        result = instance.getEnabled();
        assertEquals( expResult, result );

        expResult = false;
        instance.setEnabled( expResult );
        result = instance.getEnabled();
        assertEquals( expResult, result );

    }

    /**
     * Test of getMatchFirstByte method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testGetMatchFirstByte() {
        System.out.println( "-- getMatchFirstByte --" );
        int ch0 = 0x7E;
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);
        instance.setEnabled( true );

        //  Initially unset.
        assertEquals( instance.getMatchFirstByte(), -1 );

        instance.setMatchFirstByte( ch0 );
        assertEquals( instance.getMatchFirstByte(), ch0 );

        instance.add( (byte) 0x01 );
        instance.add( (byte) 0x02 );
        assertEquals( instance.getCount(), 0 );

        instance.add( (byte) ch0 );
        instance.add( (byte) ch0 );
        instance.add( (byte) 0x81 );
        instance.add( (byte) 0x82 );

        assertEquals( instance.getCount(), 4 );

        byte[]  expResult = { (byte) ch0, (byte) ch0, (byte) 0x81, (byte) 0x82 };
        byte[]  result = instance.disableAndGet();
        assertArrayEquals( expResult, result );
    }

    /**
     * Test of setMatchFirstByte method, of class SynchronizedByteBuffer.
     */
    @Test
    public void testSetMatchFirstByte() {
        System.out.println( "-- setMatchFirstByte --" );
        int ch0 = 0x7E;
        SynchronizedByteBuffer instance = new SynchronizedByteBuffer(55);

        //  Initially unset.
        assertEquals( instance.getMatchFirstByte(), -1 );

        instance.setMatchFirstByte( ch0 );
        assertEquals( instance.getMatchFirstByte(), ch0 );
    }

}
