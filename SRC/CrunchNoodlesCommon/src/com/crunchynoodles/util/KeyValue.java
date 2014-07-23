package com.crunchynoodles.util;

import java.util.Map;
import java.util.Objects;


/**
 *  A C#-like key-value pair with comparing!
 * @author brian
 * @see http://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/tuple/Pair.html
 */
public class KeyValue< KeyType extends Comparable, ValueType >
        implements Comparable<KeyValue< KeyType, ValueType >>,  Map.Entry< KeyType,ValueType >
{
    public KeyType  Key;
    public ValueType Value;

    /***
     *  Compare this Key-Value to another key-value.
     *  Only the {@code Key} is relevant to the compare.
     *  {@code null} references cause that object to be last.
     *
     * @param   otherkv the object to be compared.
     * @return  a negative integer, zero, or a positive integer if this object
     *          is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public int compareTo( KeyValue< KeyType, ValueType > otherkv )
    {
        if( otherkv == null )
            return +1;
        if( this.Key != null && otherkv.Key == null )
            return +1;
        if( this.Key == null && otherkv.Key != null )
            return -1;

        return this.Key.compareTo( otherkv.Key );
    }

    @Override
    public int hashCode()
    {
        // Try to use as many bits as possible.
        return (Key == null ? 0 : Key.hashCode()) ^
                (Value == null ? 0 : Value.hashCode());
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final KeyValue<KeyType, ValueType> other = (KeyValue<KeyType, ValueType>) obj;
        if( !Objects.equals( this.Key, other.Key ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder  sb = new StringBuilder();

        //  Let StringBuilder#append() deal with any null references!
        sb.append( '(' ).append(  Key ).append( ',' );
        sb.append(  Value ).append( ')' );

        return sb.toString();
    }

    // ------------------------------------------------------------------------
    //  interface Map.Entry< KeyType,ValueType > implementation.

    @Override
    public KeyType getKey()
    {
        return this.Key;
    }

    @Override
    public ValueType getValue()
    {
        return this.Value;
    }

    @Override
    public ValueType setValue( ValueType value )
    {
        this.Value = value;
        return this.Value;
    }
}
