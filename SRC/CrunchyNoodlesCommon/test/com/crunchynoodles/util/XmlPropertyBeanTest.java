/***  This file is dedicated to the public domain, 2014 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author brian
 */
public class XmlPropertyBeanTest {

    public XmlPropertyBeanTest() {
    }

    @BeforeClass
    public static void setUpClass()
            throws Exception
    {
        System.out.println( "setUpClass -- XmlPropertyBean" );
        Throwable  th;
        XmlPropertyBean  instance;

        //  "bool" throws exception if value is null or "" (empty string).
        th = null;
        instance = null;
        try {
            instance = new XmlPropertyBean("bad_bool.1", "bool", null );
        } catch (Exception e) {
            th = e;
        }
        assertNull( instance );
        assertNotNull( th );
        assertTrue( th instanceof NullPointerException );

        //  "bool" throws exception if value is null or "" (empty string).
        th = null;
        instance = null;
        try {
            instance = new XmlPropertyBean("bad_bool.2", "bool", "" );
        } catch (Exception e) {
            th = e;
        }
        assertNull( instance );
        assertNotNull( th );
        assertTrue( th instanceof IllegalArgumentException );

        //  control chars in key name is illegal.
        th = null;
        instance = null;
        try {
            instance = new XmlPropertyBean("beep\b", "int", "33" );
        } catch (Exception e) {
            th = e;
        }
        assertNull( instance );
        assertNotNull( th );
        assertTrue( th instanceof IllegalArgumentException );

        //  spaces in key name is illegal.
        th = null;
        instance = null;
        try {
            instance = new XmlPropertyBean("this is spaced out", "int", "66" );
        } catch (Exception e) {
            th = e;
        }
        assertNull( instance );
        assertNotNull( th );
        assertTrue( th instanceof IllegalArgumentException );

    }

    @Before
    public void setUp() {
    }

    /**
     * Test of getElementName method, of class XmlPropertyBean.
     */
    @Test
    public void testGetElementName() {
        System.out.println( "getElementName" );
        XmlPropertyBean instance = new XmlPropertyBean("unused", "bool", "false");  // values here not used in this test.
        String expResult = "property";
        String result = instance.getElementName();
        assertEquals( expResult, result );
    }

    /**
     * Test of getAttributeList method, of class XmlPropertyBean.
     */
    @Test
    public void testGetAttributeList() {
        System.out.println( "getAttributeList" );
        XmlPropertyBean instance = new XmlPropertyBean("unused", "bool", "false");  // values here not used in this test.
        List<String> expResult = Arrays.asList( "key", "type" );
        List<String> result = instance.getAttributeList();
        assertEquals( expResult, result );
    }

    /**
     * Test of toString method, of class XmlPropertyBean.
     */
    @Test
    public void testToString() {
        System.out.println( "toString.01" );
        XmlPropertyBean instance = new XmlPropertyBean("key_value", "bool", "false");
        String expResult = "{Key=\"" + instance.getKey() + "\",Type=" + instance.getType() + ",Value=\"false\"}" ;
        String result = instance.toString();
        assertEquals( expResult, result );

        System.out.println( "toString.02" );
        instance = new XmlPropertyBean("key_value", "bool", "yes");
        expResult = "{Key=\"" + instance.getKey() + "\",Type=" + instance.getType() + ",Value=\"true\"}" ;
        result = instance.toString();
        assertEquals( expResult, result );

        System.out.println( "toString.03" );
        instance = new XmlPropertyBean("key_value", "hexbytes", "000102030420304050C0D0E0F0FF");
        expResult = "{Key=\"" + instance.getKey() + "\",Type=" + instance.getType() + ",Value=(hex)[000102030420304050C0D0E0F0FF]}" ;
        result = instance.toString();
        assertEquals( expResult, result );

    }

    /**
     * Test of hashCode method, of class XmlPropertyBean.
     */
    @Test
    public void testHashCode() {
        System.out.println( "hashCode -- skipped" );
    }

    /**
     * Test of equals method, of class XmlPropertyBean.
     */
    @Test
    public void testEquals() {
        System.out.println( "equals -- skipped" );
//        Object obj = null;
//        XmlPropertyBean instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals( obj );
//        assertEquals( expResult, result );
    }

    /**
     * Test of getKey method, of class XmlPropertyBean.
     */
    @Test
    public void testGetKey() {
        System.out.println( "getKey" );
        XmlPropertyBean instance = new XmlPropertyBean("roadrunner", "int", "33");
        String expResult = "roadrunner";
        String result = instance.getKey();
        assertEquals( expResult, result );
    }

    /**
     * Test of getType method, of class XmlPropertyBean.
     */
    @Test
    public void testGetType() {
        System.out.println( "getType" );
        XmlPropertyBean instance = new XmlPropertyBean("unused", "int", "33");
        String expResult = "int";
        String result = instance.getType();
        assertEquals( expResult, result );
    }

    /**
     * Test of getValue method, of class XmlPropertyBean.
     */
    @Test
    public void testGetValue() {
        System.out.println( "getValue" );
        XmlPropertyBean instance = new XmlPropertyBean("unused", "int", "33");
        Integer expResult = 33;
        Object result = instance.getValue();
        assertEquals( expResult, result );
    }

}
