/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import com.crunchynoodles.util.exceptions.DuplicateKeyException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Holds the property list that contains little XmlPropertyBean objects.
 *  Each property has a string-value and a "type".
 *  The property list has no attributes of its own.
 *
 * @author brian
 */
public abstract class AbstractXmlEntityWithPropertiesBean
    implements XmlEntityBean
{

    public AbstractXmlEntityWithPropertiesBean()
    {
        // placeholder...
    }

    /***
     *  Insert a new property into the list, but throws exception if property already known.
     *  The property's value is converted from a string into an internal form.
     *  If a conversion-exception occurs, then value could not be converted based
     *  on {@code typestr}'s description.
     *
     * @param key String name of key
     * @param typestr Encoding and type of value
     * @param value String encoding of value, it will be converted ; must not be null.
     *
     * @throws DuplicateKeyException
     */
    public void addProperty( String key, String typestr, String value )
            throws DuplicateKeyException
    {
        if( value == null )
        {
            throw new IllegalArgumentException( "property 'value' cannot be null" );
        }
        if( hasProperty( key ) )
        {
            throw new DuplicateKeyException( key, false );
        }

        XmlPropertyBean  p = new XmlPropertyBean( key, typestr, value );

        m_map.put( p.Key, p );

        System.out.println( "... new property: " + p.toString() );
    }

    /***
     *  Store copy of an {@code XmlPropertyBean} from the caller.
     *
     * @param pb Caller's bean that is shallow-cloned.
     */
    public void addProperty( XmlPropertyBean pb )
    {
        addProperty( pb.getKey(), pb.getType(), pb.getValue().toString() );
    }

    /***
     *  Checks this propertyList for a property, by {@code key}.
     *  If found, then returns a copy of whole property ; otherwise null.
     *
     * @param key String name of key
     * @return Shallow-copy of property if found, otherwise null.
     */
    public XmlPropertyBean getProperty( String key )
    {
        XmlPropertyBean  p = m_map.get( key );
        if( p != null )
        {
            p = new XmlPropertyBean( p );
        }
        return p;
    }

    /***
     *  Check if property with this key is known.
     * @param key String name of key
     * @return true if known ; otherwise false
     */
    public boolean hasProperty( String key )
    {
        return m_map.containsKey( key );
    }

    /***
     *  Get the set keys.  Handle for iterating.
     * @return {@code Set<>} of key strings.
     */
    public Set<String>   keyPropertyListSet()
    {
        return m_map.keySet();
    }

    /***
     *  Return count of stored properties.
     * @return Count of properties.
     */
    public int      getPropertyListSize()
    {
        return m_map.size();
    }

    /***
     *  Removes all properties from the propertyList.
     */
    public void     clearProperties()
    {
        m_map.clear();
    }

    // ----------------------------------------------------------------------------

    @Override
    public int  hashCode()
    {
        int  h = super.hashCode();
        h ^= m_map.hashCode();
        h ^= this.getElementName().hashCode();

        return( h );
    }

    @Override
    public String  toString()
    {
        return new String( "[" + this.getElementName() + ": " + m_map.toString() + "]" );
    }

    // ----------------------------------------------------------------------------

    protected Map<String, XmlPropertyBean>  m_map = new HashMap<>();

}
