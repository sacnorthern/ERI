/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio;

import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brian
 */
public class LayoutTimeoutManagerTest {

    public LayoutTimeoutManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass()
    {
        System.out.println( "shutdownNow..." );
        LayoutTimeoutManager.getInstance().shutdownNow();
    }

    /**
     * Test of getInstance method, of class LayoutTimeoutManager.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println( "getInstance" );
        LayoutTimeoutManager instance = LayoutTimeoutManager.getInstance();
        LayoutTimeoutManager expResult = LayoutTimeoutManager.getInstance();
        LayoutTimeoutManager result = instance;

        assertEquals( expResult, result );
    }

    /**
     * Test of timeoutMicros method, of class LayoutTimeoutManager.
     */
    @Test
    public void testTimeoutMicros()
    {
        tTM1();
        tTM2();
    }

    private void tTM1()
    {
        final int  TEST_VALUE = 0;
        System.out.println( "timeoutMicros-1" );
        int  milliseconds = 10 * 1000;
        LayoutTimeoutManager.TimeoutAction notifyObject = new MyClient();
        ReentrantLock pauseLock = new ReentrantLock();
        Integer[]  done = { new Integer(TEST_VALUE) };

        LayoutTimeoutManager instance = LayoutTimeoutManager.getInstance();

        Future result = instance.timeoutMillis( milliseconds, notifyObject, done );
        int  secs = 0;
        try {
            for( secs = 1 ; ; ++secs )
            {
                System.out.print( "  " + secs );
                System.out.flush();

                Thread.sleep( 1 * 1000 );        // sleep one second.
                synchronized( done )
                {
                    if( done[0].intValue() != TEST_VALUE )
                        break;
                }
            }
        }
        catch( InterruptedException ex )
        {
            System.out.print( " !!InterruptedException!!" );
        }
        finally
        {
            if( pauseLock.isHeldByCurrentThread() )
                pauseLock.unlock();
        }
        System.out.println();
        assertTrue( "Timeout not right time-range", (8 < secs && secs < 12 ) );
    }

    private void tTM2()
    {
        final int  TEST_VALUE = 0;
        System.out.println( "timeoutMicros-2" );
        int  milliseconds = 10 * 1000;
        LayoutTimeoutManager.TimeoutAction notifyObject = new MyClient();
        ReentrantLock pauseLock = new ReentrantLock();
        Integer[]  done = { new Integer(TEST_VALUE) };

        LayoutTimeoutManager instance = LayoutTimeoutManager.getInstance();

        Future result = instance.timeoutMillis( milliseconds, notifyObject, done );
        int  secs = 0;
        boolean   flag = false;

        try {
            for( secs = 1 ; secs < 6 ; ++secs )
            {
                System.out.print( "  " + secs );
                System.out.flush();

                Thread.sleep( 1 * 1000 );        // sleep one second.
                synchronized( done )
                {
                    if( done[0].intValue() != TEST_VALUE )
                    {
                        flag = true;
                        break;
                    }
                }
            }

            if( ! flag )
                result.cancel( true );

        }
        catch( InterruptedException ex )
        {
            System.out.print( " !!InterruptedException!!" );
        }
        finally
        {
            if( pauseLock.isHeldByCurrentThread() )
                pauseLock.unlock();
        }
        System.out.println();
        assertTrue( "Timeout not right time-range", (secs == 6) );
        assertFalse( "Timeout never occurred", (flag) );

        //  Wait to 14 seconds to ensure onTimeout() not called.
        try {
            Thread.sleep( (14 - secs) * 1000 );
        }catch( InterruptedException ex )
        {
        }
        assertTrue( done[0].intValue() == TEST_VALUE );

    }


    public class MyClient implements LayoutTimeoutManager.TimeoutAction
    {
        public MyClient()
        {
            System.out.println( "MyClient object created" );
        }

        @Override
        public void onTimeout( Object anchor )
        {
            System.out.println( "  onTimeout()  " );
            synchronized( anchor )
            {
                Integer[]  arry = (Integer[]) anchor;
                arry[ 0 ] += 139;
            }
        }
    }
}
