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
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  Provides a sparse array of boolean values, where array-values can be {@code null}
 *  if never assigned.
 *  It follows a pseudo-HashMap interface.  By default, asking for an unknown
 *  element will return false.  Calling {@code containsKey} instead will return
 *  {@code null} if no value is present.
 *  Index goes from 0 to MAX-1, MAX can grow but won't shrink.
 *
 *  <p> Can create an {@code iterator<Boolean>} over the array, where unset values return
 *  the {@code null} reference.
 *
 * <p> Similar to {@code interface Collection<Boolean>} ; however, methods like
 *  {@code containsAll()} , {@code removeAll()} and {@code retainAll()} do not make sense.
 *  The passed in collection is Boolean's.  Since the table is sparse ( can contain holes ),
 *  really those methods should pass in slot numbers to remove or retain instead.
 *
 *  <p> Built out of frustration at trying to use {@code HashMap<Integer, Boolean>}.
 *  It's easier with C# cuz it boxes primitive types automagically.
 *
 *  <p> <strong>Object is not MT-safe.</strong>
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

    public TableOfBoolean( final boolean[] seed )
    {
        this( seed.length );
        this.setFrom( seed );
    }

    //--------------------------  MANAGEMENT  -------------------------

    /**
     *  Removes all of the mappings from this map.
     *  The map will be 100% empty / unset after this call returns.
     *  Afterwards, {@link containsKey()} for all slots will return {@code false}.
     *  Afterwards, {@link isEmpty()} will return {@code true}.
     */
    public void clear()
    {
        Arrays.fill( m_has_value, false );
    }

    /***
     *  Test if there are no set values in this table.
     *  After a {@link clear()}, this method returns {@code true}.
     *
     * @return true if no places have a set value.
     */
    public boolean isEmpty()
    {
        for( boolean b : m_has_value )
        {
            if( b ) return false;
        }
        return true;
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
     *  @return new array with Boolean objects , and {@code null} reference when unset.
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
                result[ j ] = m_value[j];
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
            if( hasNext() == false )            // if there are no places with "put values",
                m_index = m_has_value.length;   // .. then indicate nothing to do.

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
            while( ++m_index < m_has_value.length )
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
            TableOfBoolean.this.remove( m_index );    // .m_has_value[ m_index ] = false;
            ++m_index;
        }
    }

    //------------------------  BOOLEAN GETTORS  ----------------------

    /***
     *   Returns slot value, never {@code null}.
     *
     * @param index which slot (OK if negative or out-of-range).
     * @return {@code Boolean.TRUE} iff slot is put and it is put to TRUE , otherwise {@code Boolean.FALSE}.
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
     * @return {@code Boolean.TRUE} iff slot is put to some value.
     */
    public boolean containsKey( int index )
    {
        if( 0 <= index && index < m_value.length && m_has_value[index] )
            return true;

        return false;
    }

    /***
     *  Retrieve slot's value, or {@code null} if unset.
     * @param index which slot (OK if negative or out-of-range).
     * @return slot's value, or {@code null} if unset.
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
    public void put( int index, boolean v )
    {
        if( index < 0 )
            throw new IllegalArgumentException( "index < 0" );

        if( index >= m_value.length )
            resize( index + 1 );

        m_value[ index ] = v;
        m_has_value[ index ] = true;
    }

    /***
     *  Set values from a provided array of booleans, where slots from 0 to
     *  {@code readArray.length - 1} will be set either {@code true} or {@code false).
     *  The result will not be sparse over this slot range.
     *
     *  @param readArray array to read from, OK if {@code null}.
     */
    public void setFrom( final boolean[] readArray )
    {
        if( null != readArray )
        {
            int  j = readArray.length ;

            this.resize( j );
            while( --j >= 0 )
            {
                this.put( j, readArray[j] );
            }
        }
    }

    /***
     *  Set values from a provided Table.  If new value is put, then it updates
  existing value.  If a mismatch in size, {@code this} will expand but not
     *  shrink or be truncated.
     *
     *  @param newValues Update values to replace existing values.
     */
    public void  setFrom( final TableOfBoolean newValues )
    {
        if( null != newValues )
        {
            Boolean  nv;
            for( int j = newValues.size() ; --j >= 0 ; )
            {
                nv = newValues.getEntry( j );
                if( null != nv )
                    this.put( j, nv );
            }
        }
    }

    //------------------------  BOOLEAN OPERATORS  -----------------------

    /***
     *  Perform union (OR) with another, updating this.
     *
     * @param rhs Perform bit-wise OR with.
     */
    public void  unionWith( final TableOfBoolean rhs )
    {
        if( null != rhs )
        {
            Boolean     ll, rr;
            int         rhs_size = rhs.size();
            int         j;
            final int   new_size = Math.max( this.size(), rhs_size );
            this.resize( new_size );

            for( j = rhs_size ; --j >= 0 ; )
            {
                ll = this.getEntry( j );
                rr = rhs.getEntry( j );
                if( null != ll || null != rr )
                {
                    if( null == ll )
                        ll = Boolean.FALSE;
                    if( null == rr )
                        rr = Boolean.FALSE;

                    //  If either side has a value, then we can compute union.
                    this.put( j, ll || rr );
                }
                else
                {
                    //  Neither side has a value, so remove.
                    this.remove( j );
                }
            }
        }
    }

    /***
     *  Perform intersection (AND) with another, updating this.
     *  <strong> if {@link rhs} is smaller than {@code this}, then we shrink.</strong>
     *
     * @param rhs Boolean array
     */
    public void  intersectWith( final TableOfBoolean rhs )
    {
        if( null != rhs )
        {
            Boolean     ll, rr;
            int         rhs_size = rhs.size();
            int         j;
            final int   new_size = Math.max( this.size(), rhs_size );
            this.resize( new_size );

            for( j = rhs_size ; --j >= 0 ; )
            {
                ll = this.getEntry( j );
                rr = rhs.getEntry( j );
                if( null != ll && null != rr )
                {
                    //  When both side has a value, then we can compute intersection.
                    this.put( j, ll && rr );
                }
                else
                {
                    //  One size or other has a value, so remove.
                    this.remove( j );
                }
            }

            //  If unionWith RHS has fewer places, then shrink ourselves.
            for ( j = rhs_size ; j < this.size() ; ++j )
            {
                this.remove( j );
            }
        }
    }

    /***
     *  Compare {code this} to {@link rhs} and return {@code true} if values within
     *  are same and unset places are same, or {@code false} if there are any differences.
     *  If {@link rhs} is {@code null}, then can never be equal/same.
     *
     * <p> Tables do NOT need be the same size, as long as those places with a value
     *  have identical values, the tables will be same.
     *  Thus if {@link rhs} has twice as many slots bt both only have values in slots 3 to 12,
     *  then the tables are the same.
     *
     *  @param rhs Another table to compare to.
     *  @return true if those slots with values are identical values ; otherwise false.
     */
    public boolean isSame( final TableOfBoolean rhs )
    {
        if( rhs == null )
            return false;

        int   max_size = Math.max( this.size(), rhs.size() );

        for( int j = max_size ; --j >= 0 ; )
        {
            Boolean  ll = this.getEntry( j );
            Boolean  rr =  rhs.getEntry( j );

            //  We're dealing with references here, so "if( ll != rr )" mearly compares references,
            //  not the actual values, i.e. true or false.
            if( (ll == null && rr != null) ||
                (ll != null && rr == null) )
            {
                //  One side is null but other isn't, so not same.
                return false;
            }
            if( ll != null && rr != null && ll.compareTo(rr) != 0 )
                return false;
        }

        //  All thing examined are same, so tables are same / equal.
        return true;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /*** TRUE means we have a value at this index, so can check {@link m_value} array. */
    private boolean[]     m_has_value;

    /*** Client's value, or false if never put. */
    private boolean[]     m_value;

}
