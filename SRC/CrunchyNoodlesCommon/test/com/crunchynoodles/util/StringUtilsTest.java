/***  This file is dedicated to the public domain, 2014 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.util.ArrayList;
import java.util.Arrays;
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
        //  'expResult' has double constructors so 'expResult' can later to add()-ed to.
        List<String> expResult = new ArrayList<String>( Arrays.asList( "First", "Second has   Quotes", "last" ) );
        List<String> result = StringUtils.tokenizeQuotedStrings( withQuotes );
        if( result.size() != 3 )
            fail( "StringUtils.tokenizeQuotedStrings() --> List.size != 3" );
        assertEquals( expResult, result );

        withQuotes = withQuotes + "    lastly     \"unquoted three words  ";
        expResult.add( "lastly" );
        expResult.add( "unquoted three words  " );
        result = StringUtils.tokenizeQuotedStrings( withQuotes );
        if( result.size() != 5 )
            fail( "StringUtils.tokenizeQuotedStrings() --> List.size != 5" );
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
