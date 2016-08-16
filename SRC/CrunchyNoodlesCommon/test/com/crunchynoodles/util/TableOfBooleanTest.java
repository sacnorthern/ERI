/***  This file is dedicated to the public domain, 2016 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author brian
 */
public class TableOfBooleanTest {

    public TableOfBooleanTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
            throws Exception
    {
        System.out.println( "setUpClass -- TableOfBoolean" );
        TableOfBoolean  instance = new TableOfBoolean();
        int  len = instance.size();
        assertTrue( "non-positive size", len > 0 );

        //  All slots are initially unset.
        Object[]  arr = instance.toArray();
        for( Object o : arr )
        {
            assertNull( "Slot isn't null", o);
        }

        //  Array must be same size as how much table is holding.
        assertEquals( len, arr.length );

        assertTrue( instance.isEmpty() );
    }

    @Before
    public void setUp()
    {
        //  Called before every test method.
    }

    /**
     * Test of clear method, of class TableOfBoolean.
     */
    @Test
    public void testClear()
    {
        System.out.println( "clear" );
        TableOfBoolean instance = new TableOfBoolean();
        instance.put( 14, true);
        instance.put( 17, true);
        instance.put( 11, true);
        instance.clear();

        //  Check all slots and ensure they are unset.
        for( int j = instance.size() + 4 ; --j >= 0 ; )
        {
            if( instance.getEntry( j ) != null )
                fail( "Found valid slot at " + j );
            if( instance.containsKey( j ) )
                fail( "Contains valid data at " + j );
        }

        assertTrue( instance.isEmpty() );
    }

    /**
     * Test of resize method, of class TableOfBoolean.
     */
    @Test
    public void testResize()
    {
        System.out.println( "resize" );
        int result;
        TableOfBoolean instance = new TableOfBoolean();

        int newCapacity = 3;
        instance.resize( newCapacity );
        result = instance.size();
        assertEquals( newCapacity, result );

        newCapacity = 4449;
        instance.resize( newCapacity );
        result = instance.size();
        assertEquals( newCapacity, result );

        newCapacity = 29;
        instance.resize( newCapacity );
        result = instance.size();
        assertEquals( newCapacity, result );

    }

    /**
     * Test of toArray method, of class TableOfBoolean.
     */
    @Test
    public void testToArray()
    {
        System.out.println( "toArray" );
        TableOfBoolean instance = new TableOfBoolean();
        Object[] expResult = new Boolean[ instance.size() ];
        Object[] result = instance.toArray();
        assertArrayEquals( expResult, result );

        instance.clear();
        instance.resize( 99 );
        int[]  places = { 23, 14, 11, 83, 68, 31 };
        expResult = new Boolean[ instance.size() ];
        for( int j : places )
        {
            assertTrue( "Please change places[] to have smaller values", j < instance.size() );
            instance.put( j, true );
            expResult[ j ] = true;
        }

        result = instance.toArray();
        assertArrayEquals( expResult, result );

    }

    /**
     * Test of iterator method, of class TableOfBoolean.
     */
    @Test
    public void testIterator()
    {
        System.out.println( "iterator" );
        TableOfBoolean instance = new TableOfBoolean();
        Iterator<Boolean> expResult = null;
        Iterator<Boolean> result = instance.iterator();
//        assertEquals( expResult, result );
//        // TODO review the generated test code and remove the default call to fail.
//        fail( "The test case is a prototype." );
        System.out.println( "\t*** TO DO ***" );
    }

    /**
     * Test of get method, of class TableOfBoolean.
     */
    @Test
    public void testGet()
    {
        System.out.println( "get" );
        int index = 0;
        TableOfBoolean instance = new TableOfBoolean();
        boolean expResult = false;
        boolean result = instance.get( index );
        assertEquals( expResult, result );

        index = 19;
        result = instance.get( index );
        assertEquals( expResult, result );

        index = 33;
        result = instance.get( index );
        assertEquals( expResult, result );

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        int[]  places = { 23, 12, 55, 37, 199, 110 };
        for( int j : places )
        {
            instance.put( j, true );
        }

        //  Read back, ensuring just certain places changed.  Before and after place must be unset and false.
        Boolean  bo;
        for( int j : places )
        {
            result = instance.get( j-1 );
            assertFalse( "Before slot set", result );
            bo = instance.getEntry( j-1 );
            assertNull( "Before slot has value", bo );

            result = instance.get( j );
            assertTrue( "Slot not set", result);

            result = instance.get( j+1 );
            assertFalse( "After slot set", result );
            bo = instance.getEntry( j+1 );
            assertNull( "Before slot has value", bo );
        }
    }

    /**
     * Test of containsKey method, of class TableOfBoolean.
     */
    @Test
    public void testContainsKey()
    {
        System.out.println( "containsKey" );
        int index = 0;
        TableOfBoolean instance = new TableOfBoolean();
        boolean expResult = false;
        boolean result = instance.containsKey( index );
        assertEquals( expResult, result );

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        int[]  places = { 11, 23, 17, 55, 37, 199, 137, 444 };
        for( int j : places )
        {
            instance.put( j, true );
        }

        //  Read back, ensuring just certain places changed.  Before and after place must be unset and false.
        Boolean  bo;
        for( int j : places )
        {
            bo = instance.getEntry( j-1 );
            assertNull( "Before slot has value", bo );

            bo = instance.getEntry( j );
            assertNotNull( "Slot is unset", bo );

            bo = instance.getEntry( j+1 );
            assertNull( "Before slot has value", bo );
        }
    }

    /**
     * Test of getEntry method, of class TableOfBoolean.
     */
    @Test
    public void testGetEntry()
    {
        System.out.println( "getEntry" );
        int index = 0;
        TableOfBoolean instance = new TableOfBoolean();
        Boolean expResult = null;
        Boolean result = instance.getEntry( index );
        assertEquals( expResult, result );

        Boolean[]  false_true  = { Boolean.FALSE , Boolean.TRUE };
        for( Boolean val : false_true )
        {
            //  start fresh!
            instance.clear();

            //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
            int[]  places = { 7, 23, 17, 65, 37, 202, 501 };
            for( int j : places )
            {
                instance.put( j, val );
            }

            //  Read back, ensuring just certain places changed.  Before and after place must be unset and false.
            Boolean  bo;
            for( int j : places )
            {
                bo = instance.getEntry( j-1 );
                assertNull( "Before slot has value", bo );

                bo = instance.getEntry( j );
                assertNotNull( "Slot is unset", bo );

                assertEquals( val.booleanValue(), bo.booleanValue() );

                bo = instance.getEntry( j+1 );
                assertNull( "Before slot has value", bo );
            }
        }

    }

    /**
     * Test of remove method, of class TableOfBoolean.
     */
    @Test
    public void testRemove()
    {
        System.out.println( "remove" );
        int index = 0;
        TableOfBoolean instance = new TableOfBoolean();
        boolean expResult = false;
        boolean result = instance.remove( index );
        assertEquals( expResult, result );

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        final int[]  places = { 7, 23, 17, 65, 37, 202, 501 };
        int   now_alive = places.length;
        for( int slot : places )
        {
            instance.put( slot, true );
        }

        for( int slot : places )
        {
            instance.remove( slot );
            now_alive--;

            int  sizeP2 = instance.size() + 2;
            int  after_remove_alive = 0;

            for( int j = 0 ; j <= sizeP2 ; ++j )
            {
                if( instance.containsKey( j ) )
                    ++after_remove_alive;
            }

            assertEquals("Removed() slot still active", now_alive, after_remove_alive);
        }
    }

    /**
     * Test of put method, of class TableOfBoolean.
     */
    @Test
    public void testPut()
    {
        System.out.println( "set" );
        int index = 0;
        boolean v = false;
        TableOfBoolean instance = new TableOfBoolean();
        instance.put( index, v );

        Boolean  expResult = instance.getEntry( index );
        assertEquals( v, expResult );
    }

    /**
     * Test of size method, of class TableOfBoolean.
     */
    @Test
    public void testSize()
    {
        System.out.println( "getSize" );
        TableOfBoolean instance = new TableOfBoolean();

        int expResult = 32;
        int result = instance.size();
        assertEquals( expResult, result );

        expResult = 99;
        instance.resize( expResult );
        result = instance.size();
        assertEquals( expResult, result );

        expResult = 5;
        instance.resize( expResult );
        result = instance.size();
        assertEquals( expResult, result );

    }

    /**
     * Test of setFrom method, of class TableOfBoolean.
     */
    @Test
    public void testSetFrom_booleanArr()
    {
        System.out.println( "setFrom" );
        TableOfBoolean instance = new TableOfBoolean();

        //  Setting from a 'null' array doesn't chang anything, and TableOfBoolean()
        //  is created empty.  So by adding nothing , Table is still empty.
        boolean[] readArray = null;
        instance.setFrom( readArray );
        boolean  expResult = true;
        boolean  result = instance.isEmpty();
        assertEquals( expResult, result );

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        List<Integer> places = Arrays.asList( 7, 23, 17, 65, 37, 202, 501 );
        readArray = new boolean[999];
        for( int slot : places )
        {
            readArray[ slot ] = true ;
        }
        instance.setFrom( readArray );
        expResult = false;
        result = instance.isEmpty();
        assertEquals( expResult, result );

        for( int j = instance.size() ; --j >= 0 ; )
        {
            Boolean  b = instance.getEntry( j );
            if( places.contains( j ) )
            {
                assertNotNull( b );
                assertTrue( "["+j+"]", b.booleanValue() );
            } else
            {
                assertNotNull( b );
                assertFalse( "["+j+"]", b.booleanValue() );
            }
        }
    }

    /**
     * Test of setFrom method, of class TableOfBoolean.
     */
    @Test
    public void testSetFrom_TableOfBoolean()
    {
        System.out.println( "setFrom" );
        TableOfBoolean newValues = null;
        TableOfBoolean instance = new TableOfBoolean();
        instance.setFrom( newValues );
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals( expResult, result );

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        final int[]  places = { 7, 23, 17, 65, 37, 202, 501 };
        int   now_alive = places.length;
        newValues = new TableOfBoolean( 999 );
        for( int slot : places )
        {
            newValues.put( slot, true );
        }
        instance.setFrom( newValues );
        expResult = true;
        result = instance.isSame( newValues );
        assertEquals( expResult, result );

    }

    /**
     * Test of unionWith method, of class TableOfBoolean.
     */
    @Test
    public void testUnionWith()
    {
        System.out.println( "unionWith" );
        TableOfBoolean rhs = null;
        TableOfBoolean instance = new TableOfBoolean();
        instance.unionWith( rhs );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of intersectWith method, of class TableOfBoolean.
     */
    @Test
    public void testIntersectWith()
    {
        System.out.println( "intersectWith" );
        TableOfBoolean rhs = null;
        TableOfBoolean instance = new TableOfBoolean();
        instance.intersectWith( rhs );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of isSame method, of class TableOfBoolean.
     */
    @Test
    public void testIsSame()
    {
        System.out.println( "isSame" );
        TableOfBoolean rhs = null;
        TableOfBoolean instance = new TableOfBoolean();

        // 1.  The 'null' instance is always false.
        boolean expResult = false;
        boolean result = instance.isSame( rhs );
        assertEquals( expResult, result );

        //  2.  Two instances with 3 values each, added in different order.
        instance.put( 1, true );
        instance.put( 3, false );
        instance.put( 21, true );

        rhs = new TableOfBoolean();
        rhs.put( 21, true );
        rhs.put( 3, false );
        rhs.put( 1, true );
        expResult = true;
        result = instance.isSame( rhs );
        assertEquals( expResult, result );

   }

}
