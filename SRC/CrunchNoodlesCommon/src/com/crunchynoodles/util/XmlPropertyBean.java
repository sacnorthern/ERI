/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import com.google.common.io.BaseEncoding;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *  XML Property in a property-list who's value is the PCDATA between start and end elements.
 *  Properties are not "bound" and so there are no change events when set.
 * For example:
 * <ol>
 *  {@code <propertyList> <property key="initbytes.1" type="hexbytes">8F</property> </propertyList> }
 * </ol>
 * <hr>
 *  {@code <!ELEMENT property (#PCDATA)>} <br/>
 *  {@code <!ATTLIST property } <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; {@code key} &nbsp; {@code NMTOKEN  #REQUIRED} <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp; {@code type (bool|boolean|int|float|string|list|hexbytes|hexbinary|base64) "string" >} <br/>
 *
 * @see AbstractXmlEntityWithPropertiesBean
 * @author brian
 */
public class XmlPropertyBean
            implements XmlEntityBean
{
    public final String     MAGIC_KEY_DELIM_CHARS = ":=[]{}";

    /***
     *  Construct new XmlProperty from parts.
     *  The {@code valuestr} will be decoded from the string into appropriate thing
     *  based on {@code typestr}.
     *  There are a few chars illegal in {@link key}, see {@link MAGIC_KEY_DELIM_CHARS}.
     *  The types "string" and "list" are left as-is.
     *  A "list" is either comma- or semi-colon-separated values.
     *
     * @param keystr Key string for storing.
     * @param typestr A well-known name, "bool", "int", "float", "hexbytes".
     * @param valuestr String form of value.
     *
     * @throws IllegalArgumentException If any parameter is null.
     * @throws NumberFormatException if {@code valuestr} is null, or zero-length for numeric conversions.
     */
    public XmlPropertyBean( String keystr, String typestr, String valuestr )
    {
        if( keystr == null || typestr == null || valuestr == null )
        {
            throw new IllegalArgumentException( "key, typestr and valuestr cannot be null" );
        }
        if( keystr.contains( MAGIC_KEY_DELIM_CHARS ) )
        {
            throw new IllegalArgumentException( "key CANNOT contain delimiter chars:" + MAGIC_KEY_DELIM_CHARS );
        }

        this.Key = keystr;
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
        if( typestr.equalsIgnoreCase( "hexbytes" ) || typestr.equalsIgnoreCase( "hexbinary" ) )
        {
            //  valuestr can be upper-case or lower-case ; they are equivalent.
            int      j;
            byte[]   b = new byte[ valuestr.length() / 2 ];

            for( j = 0 ; j < valuestr.length() ; j += 2 )
            {
                b[ j / 2 ] = Byte.parseByte( valuestr.substring( j, j+1 ), 16 );
            }
            this.Value = b;
        }
        else
        if( typestr.equalsIgnoreCase( "base64" ) )
        {
            this.Value = BaseEncoding.base64().decode( valuestr );
        }
        else
        {
            // Unrecognized typestr.  Oh well, accept and store the string.
            //  Maybe an IP host address....?
            this.Value = valuestr;
        }
    }

    /***
     *  Copy constructor, a shallow copy.
     * @param other Thing to copy
     */
    public XmlPropertyBean( XmlPropertyBean other )
    {
        this.Key = other.Key;
        this.Type = other.Type;

        //  I want to clone "Value" here.  Sadly, Java clone() is broken.
        //  See http://www.artima.com/intv/bloch13.html

        //! if( this.Value instanceof Cloneable )
        //! {
        //!    this.Value = ((Cloneable) this.Value).clone();
        //! }
        //! else
        {
            this.Value = other.Value;
        }
    }

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_KEY, ATTR_TYPE, ATTR_VALUE );
    }

    // ----------------------------------------------------------------------------

    @Override
    public String toString()
    {
        //  TODO: Implement #toString() when Value is an array.
        return "[Key=\"" + Key + "\",Type=" + Type + ",Value=\"" + Value.toString() + "\"]" ;
    }

    @Override
    public int hashCode()
    {
        int   h = super.hashCode() ^ PROP_ELEMENT_NAME.hashCode();

        if( Key != null )       h ^= Key.hashCode();
        if( Type != null )      h ^= Type.hashCode();
        if( Value != null )     h ^= Value.hashCode();

        return( h );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final XmlPropertyBean other = (XmlPropertyBean) obj;
        if( !this.Key.equals( other.Key ) ) {
            return false;
        }
        if( !this.Type.equals( other.Type ) ) {
            return false;
        }
        if( !Objects.equals( this.Value, other.Value ) ) {
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------------

    public static final String PROP_ELEMENT_NAME = "property";

    public static final String ATTR_KEY        = "key";    // attribute
    public static final String ATTR_TYPE       = "type";     // attribute
    public static final String ATTR_VALUE      = "value";       // attribute

    public String   getKey() { return Key; }
    // NO - public void     setKey(String k ) { Key = k; }

    public String   getType() { return Type; }
    // NO - public void     setType(String t) { Type = t; }

    public Object   getValue() { return Value; }
    // NO - public void     setValue(Object v) { Value = v; }

    public String   Key;
    public String   Type;
    public Object   Value;

}
