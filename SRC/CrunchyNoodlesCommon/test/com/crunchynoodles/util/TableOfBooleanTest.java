/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brian
 */
public class TableOfBooleanTest {

    public TableOfBooleanTest()
    {
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
        instance.set( 14, true);
        instance.set( 17, true);
        instance.set( 11, true);
        instance.clear();

        //  Check all slots and ensure they are unset.
        for( int j = instance.getSize() + 4 ; --j >= 0 ; )
        {
            if( instance.getEntry( j ) != null )
                fail( "Found valid slot at " + j );
            if( instance.containsKey( j ) )
                fail( "Contains valid data at " + j );
        }
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
        result = instance.getSize();
        assertEquals( newCapacity, result );

        newCapacity = 4449;
        instance.resize( newCapacity );
        result = instance.getSize();
        assertEquals( newCapacity, result );

        newCapacity = 29;
        instance.resize( newCapacity );
        result = instance.getSize();
        assertEquals( newCapacity, result );

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
        int[]  places = { 23, 12, 55, 37, 199 };
        for( int j : places )
        {
            instance.set( j, true );
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
        int[]  places = { 11, 23, 17, 55, 37, 199, 444 };
        for( int j : places )
        {
            instance.set( j, true );
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

        //  Now do a couple of specific places.  DO NOT USE CONSECUTIVE PLACES!!
        int[]  places = { 7, 23, 17, 65, 37, 202, 501 };
        for( int j : places )
        {
            instance.set( j, true );
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
        int[]  places = { 7, 23, 17, 65, 37, 202, 501 };
        int   now_alive = places.length;
        for( int slot : places )
        {
            instance.set( slot, true );
        }

        for( int slot : places )
        {
            instance.remove( slot );
            now_alive--;

            int  sizeP2 = instance.getSize() + 2;
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
     * Test of set method, of class TableOfBoolean.
     */
    @Test
    public void testSet()
    {
        System.out.println( "set" );
        int index = 0;
        boolean v = false;
        TableOfBoolean instance = new TableOfBoolean();
        instance.set( index, v );

        Boolean  expResult = instance.getEntry( index );
        assertEquals( v, expResult );
    }

    /**
     * Test of getSize method, of class TableOfBoolean.
     */
    @Test
    public void testGetSize()
    {
        System.out.println( "getSize" );
        TableOfBoolean instance = new TableOfBoolean();

        int expResult = 32;
        int result = instance.getSize();
        assertEquals( expResult, result );

        expResult = 99;
        instance.resize( expResult );
        result = instance.getSize();
        assertEquals( expResult, result );

        expResult = 5;
        instance.resize( expResult );
        result = instance.getSize();
        assertEquals( expResult, result );

    }

}
