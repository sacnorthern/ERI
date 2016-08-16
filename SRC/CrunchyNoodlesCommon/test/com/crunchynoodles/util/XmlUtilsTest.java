/***  This file is dedicated to the public domain, 2014, 2016 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 *
 * @author brian
 */
public class XmlUtilsTest {

    public XmlUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    // ----------------------------------------------------------------------------

    /***
     *  Create a phony XML element.
     *
     * @param eleName Name of XML element
     * @return XML Element.
     */
    TestWithXmlElement  makeElement( String eleName )
    {
        return new TestWithXmlElement( eleName );
    }

    /***
     *  Create a phony XML element with a bunch of attribute name-value pairs.
     *  If attribute name already exists, it is replaced.
     *
     * @param eleName Name of XML element
     * @param attrName Name of first attribute to add
     * @param attrValue String value of first attribute
     * @param args Pairs of strings (optional)
     * @return new XML element
     */
    TestWithXmlElement  makeElement( String eleName, String attrName, String attrValue, String... args)
    {
        final String  PREFIX = "";
        final String  URI  = "";

        TestWithXmlElement el = new TestWithXmlElement( eleName );
        Attr  newatt = new TestWithAttr(el, PREFIX, attrName, attrName, URI, attrValue );
        el.setAttributeNode( newatt );

        if( args.length % 2 != 0 )
        {
            throw new IllegalArgumentException("must have pairs of attribute name-values");
        }

        for( int j = args.length ; j > 0 ; j -= 2 )
        {
            attrName = args[j-1];
            attrValue = args[j-2];
            newatt = new TestWithAttr(el, PREFIX, attrName, attrName, URI, attrValue );
            el.setAttributeNode( newatt );
        }

        return( el );
    }

    // ----------------------------------------------------------------------------

    /**
     * Test of XmlPrintAttrs method, of class XmlUtils.
     */
    @Test
    public void testXmlPrintAttrs() {
        System.out.println( "XmlPrintAttrs" );
        PrintStream outs = System.out;

        TestWithXmlElement entry = null;
        XmlUtils.XmlPrintAttrs( outs, entry );
        outs.println( "]] done.");

        entry = makeElement( "first" );
        XmlUtils.XmlPrintAttrs( outs, entry );
        outs.println( "]] done.");

        entry.setAttribute( "attr1", "value1" );
        XmlUtils.XmlPrintAttrs( outs, entry );
        outs.println( "]] done.");

        entry.setAttribute( "attr2", "value2" );
        XmlUtils.XmlPrintAttrs( outs, entry );
        outs.println( "]] done.");

        entry.setAttribute( "attr1", "replacement1" );
        XmlUtils.XmlPrintAttrs( outs, entry );
        outs.println( "]] done.");

        System.out.println( "XmlPrintAttrs - done." );
    }

    /**
     * Test of ParseBooleanAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseBooleanAttribute() {
        System.out.println( "ParseBooleanAttribute" );

        String attrName = "value1";
        Boolean expResult = false;
        TestWithXmlElement element = makeElement( "parseIntAttr",
                                            attrName, new StringBuilder().append( expResult ).toString() );
        Boolean result = XmlUtils.ParseBooleanAttribute( element, attrName, false );
        assertEquals( expResult, result );

         attrName = "valueTrue";
         expResult = true;
         element = makeElement( "parseIntAttr",
                                            attrName, new StringBuilder().append( expResult ).toString() );
         result = XmlUtils.ParseBooleanAttribute( element, attrName, false );
        assertEquals( expResult, result );
    }

    /**
     * Test of ParseIntegerAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseIntegerAttribute_Element_String() {
        System.out.println( "ParseIntegerAttribute" );

        String attrName = "value1";
        int expResult = 0333;
        TestWithXmlElement element = makeElement( "parseIntAttr",
                                            attrName, new StringBuilder().append( expResult ).toString() );
        int result = XmlUtils.ParseIntegerAttribute( element, attrName );
        assertEquals( expResult, result );

    }

    /**
     * Test of ParseIntegerAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseIntegerAttribute_4args() {
        System.out.println( "ParseIntegerAttribute" );

        String attrName = "value1";
        int expResult = 0333;
        TestWithXmlElement element = makeElement( "parseIntAttr",
                                            attrName, new StringBuilder().append( expResult ).toString() );
        int minValue = 12;
        int maxValue = 8191;
        int result = XmlUtils.ParseIntegerAttribute( element, attrName, minValue, maxValue );
        assertEquals( expResult, result );
    }


    /**
     * Test of ParseIntegerAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseHexBinaryCData() {
        System.out.println( "ParseIntegerAttribute" );

    }
}
