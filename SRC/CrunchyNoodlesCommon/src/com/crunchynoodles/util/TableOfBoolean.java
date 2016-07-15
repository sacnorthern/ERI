/***  Java Commons and Niceties Library from CrunchyNoodles.com
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
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  Provides a sparse array of boolean values.
 *  It follows a pseudo-HashMap interface.  By default, asking for an unknown
 *  element will return false.  Calling {@code containsKey} instead will return
 *  {@code null} if no value is present.
 *  Index goes from 0 to MAX-1, MAX can grow but won't shrink.
 *
 *  <p> Can create an iterator&lt;Boolean&gt; object over the array, where unset values return
 *  the {@code null} reference.
 *
 * <p> Similar to {@code interface Collection<Boolean>} ; however, methods like
 *  {@code containsAll()} , {@code removeAll()} and {@code retainAll()} do not make sense.
 *  The passed in collection is Boolean's.  Since the table is sparse ( can contain holes ),
 *  really these methods should pass in slot numbers to remove or retain instead.
 *
 *  <p> Built out of frustration at trying to use {@code HashMap<Integer, Boolean>}.
 *  It's easier with C# cuz it boxes primitive types automagically.
 *
 *  Object is not MT-safe.
 *
 * @see {@code interface Collection<E>}
 * @author brian
 */

public class TableOfBoolean
        implements Iterable<Boolean>
{

    //-------------------------  CONSTRUCTORS  ------------------------

    /***
     *  Create table (array) of Boolean values, with some default size.
     *  Initially, the table is full of unset values, which return {@code null}
     *  if {@link getEntry()} is called on any of the slots.
     */
    public TableOfBoolean()
    {
        this( 32 );
    }

    /***
     *  Build a table of {@code Boolean} values to your size.
     * @param init_size > 0 for indices o to <i>init_size</i> - 1.
     */
    public TableOfBoolean( int init_size )
    {
        if( init_size <= 0 )
            throw new IllegalArgumentException( "init_size <= 0" );

        m_has_value = new boolean[ init_size ];     // false here means value is "unset".
        m_value     = new boolean[ init_size ];
    }

    //--------------------------  MANAGEMENT  -------------------------

    /**
     *  Removes all of the mappings from this map.
     *  The map will be empty / unset after this call returns.
     *  Afterwards, {@link containsKey()} for all slots will return {@code false}.
     */
    public void clear()
    {
        Arrays.fill( m_has_value, false );
    }

    /***
     *  Resize our array-of-booleans.  If growing size, then new entries are "unset".
     *  Exception thrown is shrinking to 0 or a negative size.
     *  Normally {@link Arrays.copyOf()} would throw NegativeArraySizeException, but not here.
     *
     * @param newCapacity new array size.
     * @throws IllegalArgumentException if {@link newCapacity} is non-positive.
     */
    public void resize( int newCapacity )
    {
        if( newCapacity <= 0 )
            throw new IllegalArgumentException( "newCapacity <= 0" );

        m_has_value = Arrays.copyOf( m_has_value, newCapacity );
        m_value = Arrays.copyOf( m_value, newCapacity );
    }

    /***
     *  Returns current array size.  The array size will grow automatically if values are
     *  stored beyond the current size limit.  However, it will not shrink if values at the
     *  end are "unset".
     *
     * @return size of size being monitored.
     */
    public int size()
    {
        return m_value.length;
    }

    //--------------------  ITERATOR AND ITERATION  -------------------

    /***
     *  Returns an array containing all of the elements in this collection.
     *  If slot is unset, then you get null-reference.
     *  @return new array with values, not sparse.
     */
    Object[]  toArray()
    {
        int  len = m_value.length;
        Boolean[]  result = new Boolean[ len ];

        //  'result' is initially all null references.  Insert all TRUE slots.
        for( int j = len ; --j >= 0 ; )
        {
            if( m_has_value[ j ] )
            {
                result[j ] = new Boolean( m_value[j] );
            }
        }

        return( result );
    }

    /***
     *  Create iterator of Boolean objects, one for each slot.  An unset value will
     *  return {@code null} instead of {@code Boolean.TRUE} or {@code Boolean.FALSE}.
     *  Client can tell unset slots from slots with a value.  :)
     *
     * @return new iterator object.
     */
    public Iterator<Boolean> iterator()
    {
        return new TableIterator<Boolean>();
    }

    private class TableIterator<E extends Boolean> implements Iterator<E>
    {
        int  m_index;             // current slot

        TableIterator()
        {
            m_index = -1;
            if( hasNext() == false )
                m_index = m_has_value.length + 1;

        }

        @Override
        public final boolean hasNext()
        {
            // Returns true if the iteration has more elements.
            // (In other words, returns true if next() would return an element rather than throwing an exception.)
            return( m_index == -1 || m_index < m_has_value.length );
        }

        final Boolean nextEntry()
        {
            if( ++m_index < m_has_value.length )
            {
                if( m_has_value[m_index] )
                {
                    return( m_value[m_index] );
                }
            }
            //  Nothing there ( slot is unset ) so return null.
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final E next()
        {
            if( m_index >= m_has_value.length )
                throw new NoSuchElementException();

            return (E) nextEntry();
        }

        @Override
        public final void remove()
        {
            if( m_index < 0 || m_index >= m_has_value.length )
                throw new IllegalStateException();
            TableOfBoolean.this.m_has_value[ m_index ] = false;
            ++m_index;
        }
    }

    //------------------------  BOOLEAN GETTORS  ----------------------

    /***
     *   Returns slot value.
     *
     * @param index which slot (OK if negative or out-of-range).
     * @return {@code Boolean.TRUE} iff slot is set and it is set to TRUE , otherwise {@code Boolean.FALSE}.
     */
    public boolean get( int index )
    {
        if( 0 <= index && index < m_value.length )
            return m_value[ index ];

        return false;
    }

    /***
     *  Returns if slot is valid and it has some kind value there.
     *
     * @param index which slot (OK if negative or out-of-range).
     * @return {@code Boolean.TRUE} iff slot is set to some value.
     */
    public boolean containsKey( int index )
    {
        if( 0 <= index && index < m_value.length && m_has_value[index] )
            return true;

        return false;
    }

    /***
     *  Retrieve slot's value, or {@null} if unset.
     * @param index which slot (OK if negative or out-of-range).
     * @return slot's value, or {@null} if unset.
     */
    public Boolean getEntry( int index )
    {
        if( containsKey( index ) )
            return new Boolean( m_value[index] );

        return null;
    }

    /***
     *  Erase memory of storing any value at an index.
     *  Future {@link getEntry()} calls will return {@code null} for this slot.
     *
     * @param index which slot (OK if negative or out-of-range).
     * @return value last held if valid, else {@code false} if {@code index}
     *      was not recorded previously.
     */
    public boolean remove( int index )
    {
        boolean  last_value = false;

        if( 0 <= index && index < m_has_value.length )
        {
            last_value = m_value[ index ];
            m_has_value[ index ] = false;
        }

        return( last_value );
    }

    //------------------------  BOOLEAN SETTORS  ----------------------

    /***
     *  Store a value into sparse array.
     *  Valid index values are non-negative.
     *  Array will resize itself automatically if required to store new value.
     * Could throw out-of-memory exception if {@link index} is too big.
     *
     * @param index where to store.
     * @param v value to store.
     * @throws IllegalArgumentException if {@code index} &lt; 0.
     */
    public void set( int index, boolean v )
    {
        if( index < 0 )
            throw new IllegalArgumentException( "index < 0" );

        if( index >= m_value.length )
            resize( index + 1 );

        m_value[ index ] = v;
        m_has_value[ index ] = true;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /*** TRUE means we have a value at this index, so can check {@link m_value} array. */
    protected boolean[]     m_has_value;

    /*** Client's value, or false if never set. */
    protected boolean[]     m_value;

}
