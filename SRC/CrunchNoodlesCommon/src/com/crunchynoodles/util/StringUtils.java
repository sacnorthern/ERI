/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brian
 */
public class StringUtils {

    public static final char    CHAR_DQUOTE = '\"';
    public static final char    CHAR_ESCAPE = '\\';

    public static List<String> tokenizeQuotedStrings( String withQuotes )
    {
        ArrayList<String>  items = new ArrayList<String>();
        int     start, end;

        start = withQuotes.indexOf( CHAR_DQUOTE );

        //  If input doesn't begin with DQUOTE, then make it all one 'word', trimmed of spaces.
        if( start > 0 )
        {
            String before = withQuotes.substring( 0, start-1 ).trim();
            if( !StringUtils.emptyOrNull( before ) )
                items.add( before );
        }

        for( ; start >= 0 && start < withQuotes.length() ; start = withQuotes.indexOf( CHAR_DQUOTE, end+1) )
        {
            //  Find next QUOTE not preceeded by ESCAPE.
            do
            {
                end = withQuotes.indexOf( CHAR_DQUOTE, start+1 );
                if( end < 0 )
                {
                    //  Opps, the string ended without matching QUOTE.  Oh well, pretend it ended
                    //  with one.  We're all done parsing.
                    items.add( withQuotes.substring( start+1 ) );
                    break;
                }
            }
            while( withQuotes.charAt( end-1 ) == CHAR_ESCAPE );

            if( end > 0 )
                items.add( withQuotes.substring( start+1, end ) );

        }

        return items;
    }

    // ----------------------------------------------------------------------------

    /***
     *   Compare two strings for equality.  If both are null, then they are same.
     *   Uses {@code String.equals()} for equality test.
     *
     * <p> This method could be named {@code equals()} but then it might be confused
     *   with an {@code equals} method for this object-type.
     *
     * @param lhs
     * @param rhs
     * @return true if same (or both null), false when different.
     */
    public static boolean equal( String lhs, String rhs )
    {
        if( lhs == null && rhs == null )
            return true;
        if( (lhs == null && rhs != null) ||
            (lhs != null && rhs == null) )
            return false;

        //  We have two string things, so do the equals() compare.
        return lhs.equals( rhs );
    }

    // ----------------------------------------------------------------------------

    /***
     *  Check if a string is "empty", i.e. a {@code null} reference, or
     *  zero-length.
     *  If {@code obj} is not a string, then returns {@code false}.
     * @param obj
     * @return true if "empty", false otherwise.
     */
    public static boolean   emptyOrNull( String obj )
    {
        if( obj == null )
            return true;
        if( obj.length() == 0 )
            return true;
        if( "".equals( obj ) )
            return true;

        return false;
    }
}
