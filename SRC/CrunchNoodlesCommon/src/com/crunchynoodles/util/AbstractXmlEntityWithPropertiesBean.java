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
 *
 * @author brian
 */
public abstract class AbstractXmlEntityWithPropertiesBean
    implements XmlEntityBean
{

    HashMap<String,Class> flim;
    /***
     *  Insert a new property into the list, but throws error if property already known.
     *  The property's value is converted from a string into an internal form.
     *  If a conversion-exception occurs, then value could not be converted based
     *  on {@code typestr}'s instruction.
     *
     * @param key String name of key
     * @param typestr Encoding and type of value
     * @param value String encoding of value, it will be converted.
     *
     * @throws DuplicateKeyException
     */
    public void addProperty( String key, String typestr, String value )
            throws DuplicateKeyException
    {
        if( hasProperty( key ) )
        {
            throw new DuplicateKeyException( key, false );
        }

        XmlProperty  p = new XmlProperty( key, typestr, value );

        m_map.put( p.Key, p );
    }

    /***
     *  Checks propertyList for a property, by {@code key}.
     *  If found, then returns a copy of whole property ; otherwise null.
     *
     * @param key String name of key
     * @return Copy of property if found, otherwise null.
     */
    public XmlProperty getProperty( String key )
    {
        XmlProperty  p = m_map.get( key );
        if( p != null )
        {
            p = new XmlProperty( p );
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

    public class XmlProperty
    {
        public String   Key;
        public String   Type;
        public Object   Value;

        /***
         *  Construct new XmlProperty from parts.
         *  The {@code valuestr} will be decoded from the string into appropriate thing
         *  based on {@code typestr}.
         *  The types "string" and "list" are left as-is.
         *  A "list" is either comma- or semi-colon-separated values.
         *
         * @param key Key string for storing.
         * @param typestr A well-known name, "bool", "int", "float", "hexbytes".
         * @param valuestr String form of value.
         *
         * @throws IllegalArgumentException If any parameter is null.
         */
        public XmlProperty( String key, String typestr, String valuestr )
        {
            if( typestr == key || typestr == null || valuestr == null )
            {
                throw new IllegalArgumentException( "key, typestr and valuestr cannot be null" );
            }

            this.Key = key;
            this.Type = typestr;

            if( typestr.equalsIgnoreCase( "bool") || typestr.equalsIgnoreCase( "boolean") )
            {
                this.Value = Boolean.FALSE;
                if( valuestr.equalsIgnoreCase( "true") ||
                    valuestr.equalsIgnoreCase( "yes" ) )
                {
                    this.Value = Boolean.TRUE;
                }
            }
            else
            if( typestr.equalsIgnoreCase( "int" ) )
            {
                this.Value = Integer.parseInt( valuestr );
            }
            else
            if( typestr.equalsIgnoreCase( "float" ) )
            {
                this.Value = Float.parseFloat( valuestr );
            }
            else
            if( typestr.equalsIgnoreCase( "hexbytes" ) )
            {
                int      j;
                byte[]   b = new byte[ valuestr.length() / 2 ];

                for( j = 0 ; j < valuestr.length() ; j += 2 )
                {
                    b[ j / 2 ] = Byte.parseByte( valuestr.substring( j, j+1 ), 16 );
                }
                this.Value = b;
            }
            else
            {
                // Unrecognized typestr.  Oh well, accept and store the string.
                //  Maybe an IP host address....?
                this.Value = valuestr;
            }
        }

        public XmlProperty( XmlProperty other )
        {
            this.Key = other.Key;
            this.Type = other.Type;
            if( this.Value instanceof Cloneable )
            {
                this.Value = (Cloneable) this.Value;
            }
            else
            {
                this.Value = other.Value;
            }
        }

    }
    // ----------------------------------------------------------------------------

    protected Map<String, XmlProperty>  m_map = new HashMap<>();

}
