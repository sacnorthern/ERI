/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 *  Provides a sparse array of boolean values.
 *  It follows a pseudo-HashMap interface.  By default, asking for an unknown
 *  element will return false.  Calling {@ode containsKey} instead will return
 *  {@code null} if no value is present.
 *  Index goes from 0 to MAX-1, MAX can grow but won't shrink.
 *
 *  Built out of frustration at trying to use {@code HashMap&lt;Integer, Boolean&gt;}.
 *  It's easier with C# cuz it boxes primitive types automagically.
 *
 *  Object is not MT-safe.
 *
 * @author brian
 */
public class TableOfBoolean {

    //-------------------------  CONSTRUCTORS  ------------------------

    public TableOfBoolean()
    {
        this( 32 );
    }

    public TableOfBoolean( int init_size )
    {
        if( init_size < 0 )
            throw new IllegalArgumentException( "init_size < 0" );

        m_has_value = new boolean[ init_size ];
        m_value = new boolean[ init_size ];
    }

    //--------------------------  MANAGEMENT  -------------------------

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    public void clear()
    {
        Arrays.fill( m_has_value, false );
    }

    public void resize( int newCapacity )
    {
        if( newCapacity < 0 )
            throw new IllegalArgumentException( "newCapacity < 0" );

        m_has_value = Arrays.copyOf( m_has_value, newCapacity );
        m_value = Arrays.copyOf( m_value, newCapacity );
    }

    //--------------------  ITERATOR AND ITERATION  -------------------

    public Iterator<Boolean> iterator()
    {
        return new TableIterator<>();
    }

    private class TableIterator<E extends Boolean> implements Iterator<E>
    {
        int  index;             // current slot

        TableIterator()
        {
            index = -1;
            if( hasNext() == false )
                index = m_has_value.length + 1;

        }

        @Override
        public final boolean hasNext() {
            return( index == -1 || index < m_has_value.length );
        }

        final Boolean nextEntry()
        {
            while( ++index < m_has_value.length )
            {
                if( m_has_value[index] )
                {
                    return new java.lang.Boolean( m_value[index] );
                }
            }

            return null;
        }

        @Override
        public final E next()
        {
            return (E) nextEntry();
        }

        @Override
        public final void remove()
        {
            if( index < 0 )
                throw new IllegalStateException();
            TableOfBoolean.this.m_has_value[ index ] = false;
            ++index;
        }
    }

    //------------------------  BOOLEAN GETTORS  ----------------------

    public boolean get( int index )
    {
        if( index < m_value.length )
            return m_value[ index ];

        return false;
    }

    public boolean containsKey( int index )
    {
        if( index < m_value.length && m_has_value[index] )
            return true;

        return false;
    }

    public Boolean getEntry( int index )
    {
        if( containsKey( index ) )
            return new Boolean( m_value[index] );

        return null;
    }

    /***
     *  Erase memory of storing any value at an index.
     *  If a value was stored, then returns method else {@code false}.
     *
     * @param index which value to forget.
     * @return value last held if valid, else {@code false} if {@code index}
     *      was not recorded previously.
     */
    public boolean remove( int index )
    {
        boolean  last_value = false;
        if( index < m_has_value.length )
        {
            last_value = m_value[ index ];
            m_has_value[ index ] = false;
        }

        return( last_value );
    }

    //------------------------  BOOLEAN GETTORS  ----------------------

    /***
     *  Store a value into sparse array.
     *  Array will resize itself it required to store new value.
     * @param index from 0 to MAX-1.
     * @param v value to store.
     */
    public void set( int index, boolean v )
    {
        if( index < 0 )
            throw new IllegalArgumentException( "index < 0" );

        if( index >= m_value.length )
            resize( index );

        m_value[ index ] = v;
        m_has_value[ index ] = true;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /*** TRUE means we have a value at this index. */
    transient boolean[]     m_has_value;

    /*** Client's value, or false if never set. */
    transient boolean[]     m_value;

}