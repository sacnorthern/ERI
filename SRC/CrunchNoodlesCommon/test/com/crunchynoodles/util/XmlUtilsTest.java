/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import java.io.PrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    /**
     * Test of XmlPrintAttrs method, of class XmlUtils.
     */
    @Test
    public void testXmlPrintAttrs() {
        System.out.println( "XmlPrintAttrs" );
        PrintStream outs = null;
        Node entry = null;
        XmlUtils.XmlPrintAttrs( outs, entry );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of ParseBooleanAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseBooleanAttribute() {
        System.out.println( "ParseBooleanAttribute" );
        Element element = null;
        String attrName = "";
        Boolean expResult = null;
        Boolean result = XmlUtils.ParseBooleanAttribute( element, attrName );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of ParseIntegerAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseIntegerAttribute_Element_String() {
        System.out.println( "ParseIntegerAttribute" );
        Element element = null;
        String attrName = "";
        int expResult = 0;
        int result = XmlUtils.ParseIntegerAttribute( element, attrName );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of ParseIntegerAttribute method, of class XmlUtils.
     */
    @Test
    public void testParseIntegerAttribute_4args() {
        System.out.println( "ParseIntegerAttribute" );
        Element element = null;
        String attrName = "";
        int minValue = 12;
        int maxValue = 8191;
        int expResult = 0;
        int result = XmlUtils.ParseIntegerAttribute( element, attrName, minValue, maxValue );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

}
