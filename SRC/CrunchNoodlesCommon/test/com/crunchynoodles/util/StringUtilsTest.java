/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author brian
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.out.println( "StringUtilsTest#setUpClass()" );
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println( "StringUtilsTest#tearDownClass()" );
    }

    /**
     * Test of tokenizeQuotedStrings method, of class StringUtils.
     */
    @Test
    public void testTokenizeQuotedStrings() {
        System.out.println( "tokenizeQuotedStrings" );

        String withQuotes = "   First  \"Second has   Quotes\"  last";
        List<String> expResult = new ArrayList<String>() { "" };
        List<String> result = StringUtils.tokenizeQuotedStrings( withQuotes );
        if( result.size() != 3 )
            fail( "StringUtils.tokenizeQuotedStrings() --> List.size != 3" );
        assertEquals( expResult, result );
    }

    /**
     * Test of equal method, of class StringUtils.
     */
    @Test
    public void testEqual() {
        System.out.println( "equal" );

        String lhs = "this";
        String rhs = "this";
        boolean expResult = true;
        boolean result = StringUtils.equal( lhs, rhs );
        assertEquals( expResult, result );

         lhs = "this";
         rhs = "that";
         expResult = false;
         result = StringUtils.equal( lhs, rhs );
        assertEquals( expResult, result );

         lhs = null;
         rhs = "that";
         expResult = false;
         result = StringUtils.equal( lhs, rhs );
        assertEquals( expResult, result );

         lhs = null;
         rhs = null;
         expResult = false;
         result = StringUtils.equal( lhs, rhs );
        assertEquals( expResult, result );
    }

    /**
     * Test of emptyOrNull method, of class StringUtils.
     */
    @Test
    public void testEmptyOrNull() {
        System.out.println( "emptyOrNull" );
        String obj = "";
        boolean expResult = true;
        boolean result = StringUtils.emptyOrNull( obj );
        assertEquals( expResult, result );

         obj = "things";
         expResult = false;
         result = StringUtils.emptyOrNull( obj );
        assertEquals( expResult, result );

         obj = null;
         expResult = true;
         result = StringUtils.emptyOrNull( obj );
        assertEquals( expResult, result );
    }

}
