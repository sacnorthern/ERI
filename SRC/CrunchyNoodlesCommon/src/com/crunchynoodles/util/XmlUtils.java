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

import com.crunchynoodles.util.exceptions.MissingDataException;
import com.google.common.io.BaseEncoding;
import java.io.PrintStream;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *  Collection of useful methods for XML and org.w3c.dom handling. <p>
 *
 *  In parsing methods, both element and attribute must be valid references.
 *  If not, then method throws a null-pointer exception.
 *
 * @author brian
 */
public class XmlUtils {

    /***
     *  Print every attribute and value of a node, one line at a time.
     *  If {@link entry} is null, then outputs "&lt;null/&gt;".
     *
     * @param outs where to send output, cannot be null.
     * @param entry XML node to examine, OK if null.
     */
    public static void XmlPrintAttrs( PrintStream outs, Node entry )
    {
        if( entry == null )
        {
            outs.print( "<null/>" );
            return ;
        }

        //  see http://www.w3.org/2003/01/dom2-javadoc/org/w3c/dom/NamedNodeMap.html
        NamedNodeMap attrs = entry.getAttributes();

        for( int k=attrs.getLength() ; --k >= 0 ; )
        {
            Node n = attrs.item( k );
            outs.printf( "+++ has attr %s = %s\n", n.getNodeName(), n.getNodeValue() );
        }
    }

    // ----------------------------------------------------------------------------

    /***
     *  Ask element for the named attribute, then try and convert the value into a boolean.
     *  An empty or unconvertable attribute string-value returns value {@code if_not_present} instead. <p>
     *
     *  NOTE: <em>The default value should really be provided by the DTD instead of directly
     *     by the caller.</em>
     *
     * @param element XML element, must not be {@code null}.
     * @param attrName string name of attribute on element to parse.
     * @param if_not_present Default return value.
     * @return {@code true} if value is "ok", "true" or "yes" ; {@code false} otherwise.
     */
    public static Boolean ParseBooleanAttribute( Element element, String attrName, boolean if_not_present )
    {
        Boolean  val = if_not_present;
        String strval = element.getAttribute( attrName );

        if( ! StringUtils.emptyOrNull( strval ) )
        {
            if( strval.equalsIgnoreCase( "true") ||
                strval.equalsIgnoreCase( "yes" ) ||
                strval.equalsIgnoreCase( "ok" ) )
            {
                val = Boolean.TRUE;
            }
        }

        return val;
    }

    /***
     *  Ask element for the named attribute, then try and convert the value into an integer.
     *  Uses {@link Integer#parseInt(java.lang.String)} for the heavy lifting.
     *  Assumes base 10 unless value starts with "0x" which means hex.
     *  An empty string returns 0.
     *
     * @param element XML element, must not be null.
     * @param attrName string name of attribute of element to parse.
     * @return {@code int} value, when possible.
     * @throws NumberFormatException Attribute's value is not a valid integer.
     */
    public static int ParseIntegerAttribute( Element element, String attrName )
            throws NumberFormatException
    {
        int       val = 0;
        String strval = element.getAttribute( attrName );

        if( ! StringUtils.emptyOrNull( strval ) )
        {
            int  radix = 10;
            if( strval.length() > 2 && strval.startsWith( "0x" ) )
            {
                radix = 16;
                strval = strval.substring( 2 );
            }

            val = Integer.parseInt( strval, radix );
        }

        return val;
    }

    /***
     *  Ask element for the named attribute, then try and convert the value into an integer.
     *  Uses {@code Integer.parseInt()} for the heavy lifting.
     *  Assumes base 10 unless value starts with "0x" which means hex.
     *  An empty or null attribute-value returns 0.
     *
     * @param element XML element, must not be {@code null}.
     * @param attrName string name of attribute of element
     * @param minValue smallest value that is acceptable, OK if {@code Integer.MIN_VALUE}.
     * @param maxValue largest value that is acceptable, OK if {@code Integer.MAX_VALUE}.
     *
     * @return {@code int} value, when possible.
     * @throws NumberFormatException Attribute's value is not a valid integer.
     * @throws AssertionError  If value is outside of given min-max range.
     */
    public static int ParseIntegerAttribute( Element element, String attrName, int minValue, int maxValue )
            throws NumberFormatException
    {
        int       val = 0;
        String strval = element.getAttribute( attrName );

        if( ! StringUtils.emptyOrNull( strval ) )
        {
            int  radix = 10;
            if( strval.length() > 2 && strval.startsWith( "0x" ) )
            {
                radix = 16;
                strval = strval.substring( 2 );
            }

            val = Integer.parseInt( strval, radix );

            if( minValue != Integer.MIN_VALUE && val < minValue )
                throw new AssertionError( "parsed value too small", null );
            if( maxValue != Integer.MAX_VALUE && val > maxValue )
                throw new AssertionError( "parsed value too large", null );
        }

        return val;
    }

    /***
     *  Parse the binary data that is the CDATA for an element.
     *  Various "text" formats are accepted, e.g. "hexbinary" and "base64".
     *  Binary-hex may have an optional "0x" prefix to the CDATA.
     *  Upper-case and lower-case letters are equivalent for "hexbinary" but not "base64".
     * <p>
     *  If there is no CDATA, then throws {@link MissingDataException}.
     *  If CDATA string is poorly formatted,
     *  then throws {@link NumberFormatException}.
     *
     * @param element Element holding some CDATA, must not be {@code null}.
     * @param format E.g. "hexbinary", "hexbytes" or "base64", not case-sensitive.
     * @return array of bytes.
     * @throws NumberFormatException Attribute's value is not a hex data.
     */
    public static byte[] ParseHexBinaryCData( Element element, String format )
            throws NumberFormatException, MissingDataException
    {
        String   cdata_str = element.getNodeValue();

        System.out.printf(  "ParseHexBinaryCData(%s, %s) got %s\n",
                element.getNodeName(), format, cdata_str );

        if( cdata_str == null )
            throw new MissingDataException( element );

        //  Remove punctuation, spaces and end-of-line stuff
        //  see http://stackoverflow.com/questions/7552253/how-to-remove-special-characters-from-an-string
        cdata_str = cdata_str.replaceAll( "[:\\.,\\r\\n\\s ]", "" );

        if( format.equalsIgnoreCase( "hexbytes" ) || format.equalsIgnoreCase( "hexbinary" ) )
        {
            //  Remove a gratiditous "0x" prefix.
            if( cdata_str.length() > 2 && cdata_str.startsWith( "0x" ) )
            {
                cdata_str = cdata_str.substring( 2 );
            }

            //  Odd length gets a "0" on the front.  E.g. "3D0" instead of "03D0".
            if( (cdata_str.length() & 1) != 0 )
            {
                cdata_str = "0" + cdata_str;
            }

            byte[]   data = new byte[ cdata_str.length() / 2 ];
            byte     by;

            // Not using BaseEncoding.base64().decode() here since its input alphabet is strict,
            // i.e. it wants all upper-case letters.  Here, want to be more flexible.

            for( int j = 0 ; j < cdata_str.length() ; j += 2 )
            {
                try {
                    by = Byte.parseByte( cdata_str.substring( j, j + 2) );
                }
                catch( IndexOutOfBoundsException ex )
                {
                    //  Occcurs when CDATA is an odd length, which should never happen.
                    throw new NumberFormatException( "bad CDATA length for element \"" + element.getNodeName() + "\"" );
                }

                data[ j / 2 ] = by;
            }

            return( data );
        }   // end if format is hex-bytes..

        if( format.equalsIgnoreCase( "base64" ) )
        {
            return BaseEncoding.base64().decode( cdata_str );
        }   // end if format is base-64..

        throw new IllegalArgumentException( "Unknown encoding format: " + format );
    }

    // ----------------------------------------------------------------------------
}
