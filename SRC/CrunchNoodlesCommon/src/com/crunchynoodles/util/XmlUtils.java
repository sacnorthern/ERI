/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.io.PrintStream;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author brian
 */
public class XmlUtils {

    /***
     *  Print the attributes of a node, one line at a time.
     * @param outs where to send out
     * @param entry XML node to read from
     */
    public static void XmlPrintAttrs( PrintStream outs, Node entry )
    {
        NamedNodeMap attrs = entry.getAttributes();

        for( int k=attrs.getLength() ; --k >= 0 ; )
        {
            Node n = attrs.item( k );
            outs.printf( "+++ has attr %s = %s\n", n.getNodeName(), n.getNodeValue() );
        }
    }

    /***
     *  Ask element for the named attribute.  Try and convert the value into a boolean.
     *  An empty attribute value returns {@code false}.
     *
     * @param element XML element
     * @param attrName string name of attribute on element
     * @return {@code true} if value is "true" or "yes", {@code false} otherwise.
     */
    public static Boolean ParseBooleanAttribute( Element element, String attrName )
    {
        Boolean  val = Boolean.FALSE;
        String strval = element.getAttribute( attrName );

        if( ! StringUtils.emptyOrNull( strval ) )
        {
            if( strval.equalsIgnoreCase( "true") ||
                strval.equalsIgnoreCase( "yes" ) )
            {
                val = Boolean.TRUE;
            }
        }

        return val;
    }

    /***
     *  Ask element for the named attribute.  Try and convert the value into an integer.
     *  Uses {@code Integer.parseInt()} for the heavy lifting.
     *  An empty string returns 0.
     *
     * @param element XML element
     * @param attrName string name of attribute of element
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
            val = Integer.parseInt( strval );
        }

        return val;
    }

    /***
     *  Ask element for the named attribute.  Try and convert the value into an integer.
     *  Uses {@code Integer.parseInt()} for the heavy lifting.
     *  An empty string returns 0.
     *
     * @param element XML element
     * @param attrName string name of attribute of element
     * @param minValue smallest value that is acceptable, OK if {@code Integer.MIN_VALUE}.
     * @param maxVault largest value that is acceptable, OK if {@code Integer.MIN_VALUE}.
     *
     * @return {@code int} value, when possible.
     * @throws NumberFormatException Attribute's value is not a valid integer.
     * @throws AssertionError  If value is outside of min-max range.
     */
    public static int ParseIntegerAttribute( Element element, String attrName, int minValue, int maxValue )
            throws NumberFormatException
    {
        int       val = 0;
        String strval = element.getAttribute( attrName );

        if( ! StringUtils.emptyOrNull( strval ) )
        {
            val = Integer.parseInt( strval );

            if( minValue != Integer.MIN_VALUE && val < minValue )
                throw new AssertionError( "parsed value too small", null );
            if( maxValue != Integer.MAX_VALUE && val > maxValue )
                throw new AssertionError( "parsed value too large", null );
        }

        return val;
    }

}
