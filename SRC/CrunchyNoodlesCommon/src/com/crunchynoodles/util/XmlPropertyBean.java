/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2014, 2016 in USA by Brian Witt , bwitt@value.net
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

/* Code here requires Java 1.7 */

package com.crunchynoodles.util;

import com.google.common.io.BaseEncoding;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *  XML Property in a property-list who's value is the PCDATA between start and end elements.
 *  Properties are not "bound" and so there are no change events when set.
 *  The "type=" attribute can imply the value's format, e.g. hex-bytes.
 * For example:
 * <ol>
 *  {@code <propertyList> <property key="initbytes.1" type="hexbytes">8FF0</property> </propertyList> }
 * </ol>
 * <hr>
 *  {@code <!ELEMENT property (#PCDATA)>} <br>
 *  {@code <!ATTLIST property } <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; {@code key} &nbsp; {@code NMTOKEN  #REQUIRED} <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; {@code type (bool|boolean|int|float|string|list|hexbytes|hexbinary|base64) "string" >} <br>
 *
 *  <p> FUCKING byte's ARE SIGNED IN JAVA !!!  CONVERTING "F0" RESULTS IN {@link NumberFormatException}!!
 *
 * @see AbstractXmlEntityWithPropertiesBean
 * @author brian
 */
public class XmlPropertyBean
            implements XmlEntityBean
{
    /***  These char's cannot be in the {@code Key} string. Whitespace is also checked and will be rejected */
    public final static String     MAGIC_KEY_DELIM_CHARS = ":;=\"[]{}" ;

    /***
     *  Construct new XmlProperty from parts.
     *  The {@code valuestr} will be decoded from the string into appropriate thing
     *  based on {@code typestr}.
     *  The types "string" and "list" are left as-is. They can be zero-length but not {@code null}.
     *  A "list" is either comma- or semi-colon-separated values. <p>
     *
     * <p>Try to use alpha-numeric chars in {@code keystr}, and not those in {@link #MAGIC_KEY_DELIM_CHARS}.
     *  Also white-space and ISO-control chars are rejected.
     *  Trying to use them will throw {@link IllegalArgumentException} exception.
     *  "bool" affirmative is either "yes" or "true".  All other values mean "false".
     *  <em>However, an empty setting for bool is an exception.</aem>
     *
     * @param keystr Key string for storing.
     * @param typestr A well-known name, "bool" or "boolean", "int", "float", "hexbytes", "base64".
     * @param valuestr String form of value.
     *
     * @throws NullPointerException  If any parameter is null.
     * @throws IllegalArgumentException if key contains illegal chars.
     * @throws IllegalArgumentException if typestr is "bool" and valuestr is "" (zero length).
     * @throws NumberFormatException if {@code valuestr} zero-length or invalid for numeric conversions.
     */
    public XmlPropertyBean( String keystr, String typestr, String valuestr )
    {
        _validateKeyAndTypeStrings( keystr, typestr );
        if( valuestr == null )
        {
            throw new NullPointerException ( "valuestr cannot be null" );
        }

        this.Key = keystr;
        this.Type = typestr;

        //  Change string-representation into internal object-type.  Can throw format-exception to caller.
        //  Unknown 'typestr' is stored as String.
        if( typestr.equalsIgnoreCase( "bool") || typestr.equalsIgnoreCase( "boolean") )
        {
            if( valuestr.length() == 0 )
                throw new IllegalArgumentException( "boolean value cannot be empty" );

            Boolean  res = Boolean.FALSE;
            if( valuestr.equalsIgnoreCase( "true") ||
                valuestr.equalsIgnoreCase( "yes" ) )
            {
                res = Boolean.TRUE;
            }
            //  marking 'Value' as final means we only get one chance to assign to it.
            this.Value = res;
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
            //  Fucking 'byte' in Java is signed, so use array of short's instead ( a little wasterful but memory is cheap ).
            //  Using short[] means we can tell apart "hexbytes" from "base64".
            short[]   s = new short[ valuestr.length() / 2 ];

            for( int j = 0 ; j < valuestr.length() ; j += 2 )
            {
                String  pair = valuestr.substring( j, j+2 );
                s[ j / 2 ] = (short) (Integer.parseInt( pair, 16 ) & 0x00FF);
            }
            this.Value = s;
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
     *  Adopt an already-converted type-object.
     *
     * @param keystr key string (name)
     * @param typestr Permitted type valueObj was converted from.
     * @param valueObj already-converted value.
     */
    public XmlPropertyBean( String keystr, String typestr, Object valueObj )
    {
        _validateKeyAndTypeStrings( keystr, typestr );

        this.Key = keystr;
        this.Type = typestr;
        this.Value = valueObj;
    }

    /***
     *  Copy constructor, a shallow copy....
     * @param other Thing to copy
     */
    public XmlPropertyBean( XmlPropertyBean other )
    {
        _validateKeyAndTypeStrings( other.Key, other.Type );

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

    /***
     *   Validate the key-name and the type-name.  If bad, then exceptions are thrown.
     *
     * @param keyname string to check.  Cannot be null or contain spaces.
     * @param typename string to check.  Cannot be null.
     *
     * @throws NullPointerException  If any parameter is null.
     * @throws IllegalArgumentException if key contains illegal chars.
     */
    protected static void _validateKeyAndTypeStrings( String keyname, String typename )
    {

        if( keyname == null || typename == null )
        {
            throw new NullPointerException ( "key and typestr cannot be null" );
        }
        if( keyname.contains( MAGIC_KEY_DELIM_CHARS ) )
        {
            throw new IllegalArgumentException( "key CANNOT contain delimiter chars: " + MAGIC_KEY_DELIM_CHARS );
        }
        for( char c : keyname.toCharArray() )
        {
            if( Character.isWhitespace( c ) || Character.isISOControl( (int) c ) )
                throw new IllegalArgumentException( "key CANNOT contain space or control chars." );
        }

    }

    // ----------------------------------------------------------------------------

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_KEY, ATTR_TYPE );
    }

    // ----------------------------------------------------------------------------

    @Override
    public String toString()
    {
        String  firstPart = "{Key=\"" + Key + "\",Type=" + Type + ",Value=";

        if( Value == null )
        {
            firstPart += NULL_OBJECT_REF_STRING;
        }
        else
        {
            if( Value instanceof short[] )
            {
                //  {@code if( typestr.equalsIgnoreCase( "hexbytes" ) || typestr.equalsIgnoreCase( "hexbinary" ) ) }
                //  A hexbyte array !
                short[]  arr = (short[]) Value;
                StringBuilder  sb = new StringBuilder( 12 + arr.length * 2 );
                sb.append( "(hex)[" );
                for( int v : arr )
                {
                    String  str = String.format( "%02X", v );
                    sb.append( str );
                }
                sb.append( ']' );
                firstPart += sb.toString();
            }
            else
            if( Value instanceof byte[] )
            {
                //  {@code if( typestr.equalsIgnoreCase( "base64" ) ) }
                //  A hexbyte array !
                byte[]  arr = (byte[]) Value;
                StringBuilder  sb = new StringBuilder( 12 + arr.length * 2 );
                sb.append( "(base64)[" );
                for( int v : arr )
                {
                   String  str = String.format( "%02x", v );
                    sb.append( str );
                }
                sb.append( ']' );
                firstPart += sb.toString();
            }
            else
            if( Value.getClass().isArray() )
            {
                // TODO: Not sure what to do here.  How to determine type in the array?
                //  maybe http://stackoverflow.com/questions/219881/java-array-reflection-isarray-vs-instanceof
                //  maybe http://stackoverflow.com/questions/11107812/how-to-check-if-an-object-is-an-array-of-a-certain-type

                firstPart += Value.toString();
            }
            else
            if( Value instanceof Boolean )
            {
                Boolean  b = (Boolean) Value;
                if( b )
                    firstPart += "\"true\"";
                else
                    firstPart += "\"false\"";
            }
            else
            {
                firstPart += "\"" + Value.toString() + "\"" ;
            }
        }

        return  firstPart + "}" ;
    }

    @Override
    public int hashCode()
    {
        int   h = super.hashCode() ^ PROP_ELEMENT_NAME.hashCode();

        if( Key != null )       h ^= Key.hashCode() << 1;
        if( Type != null )      h ^= Type.hashCode() << 2;
        if( Value != null )     h ^= Value.hashCode() << 3;

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
        return Objects.equals( this.Value, other.Value );
    }

    // ----------------------------------------------------------------------------

    public static final String PROP_ELEMENT_NAME = "property";

    public static final String ATTR_KEY        = "key";    // attribute
    public static final String ATTR_TYPE       = "type";     // attribute

    /***  Name for this value.
     *  @return String key name.
     */
    public String   getKey() { return Key; }
    // NO - public void     setKey(String k ) { Key = k; }

    /***  Return string telling internal-type {@code Value} should be stored using.
     *  @return String naming the object-class type.
     */
    public String   getType() { return Type; }
    // NO - public void     setType(String t) { Type = t; }

    /*** Return object holding value.
     *  @return some object holding (dynamic) value.
     */
    public Object   getValue() { return Value; }
    // NO - public void     setValue(Object v) { Value = v; }

    // ----------------------------------------------------------------------------

    /*** The name of this property. */
    public final String   Key;

    /*** Internal type the value should be, e.g. "hex" which creates a byte array, or "float" to store a floating-point value. */
    public final String   Type;

    /*** Stored value.  If {@code Type} is "float", then {@code Value} is an object of
     *  class {@link java.lang.Float} representing the interchange value in the XML file.
     */
    public final Object   Value;

}
