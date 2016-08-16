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
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *   Static methods to help work with Strings.  No instance creation allowed.
 *
 * @author brian
 */
public class StringUtils {  ; // no instances.

    public static final char    CHAR_DQUOTE = '\"';
    public static final char    CHAR_ESCAPE = '\\';

    /***
     *  Tokenize a string, maybe with quoted tokens.  Tokens are white-space separated.
     *  If a quoted string is not closed, then the end-of-string implicitly ends token.
     *  The quotes, which can be single or double, are not stored in token list.
     *
     * @see <a href="http://stackoverflow.com/questions/3366281/tokenizing-a-string-but-ignoring-delimiters-within-quotes">tokenizing-a-string-but-ignoring-delimiters-within-quotes</a>
     * @param withQuotes String with some quoted-tokens.
     * @return List of tokens found.
     */
    public static List<String> tokenizeQuotedStrings( String withQuotes )
    {
        ArrayList<String>  items = new ArrayList<String>();

        // ORIG final String    regex = "\"([^\"]*)\"|(\\S+)";

        //  Group 2: Check for unterminated quoted string: assume that missing close of quotation.
        final String    regex = "\"([^\"]*)\"|\"([^\"]*)$|(\\S+)";

        /***
         * There are 2 alternates:
         *   - The first alternate matches the opening double quote, a sequence of anything
         *     but double quote (captured in group 1), then the closing double quote.
         *   - The third alternate matches any sequence of non-whitespace characters,
         *     captured in group 3.
         *   - The order of the alternates matter in this pattern.
         *
         * Note that this does not handle escaped double quotes within quoted segments.
         *
         * Inserted group 2 grabs an un-terminated quoted sub-string.
         */

        Matcher m = Pattern.compile(regex).matcher(withQuotes);
        while (m.find()) {
            if (m.group(1) != null) {
                items.add( m.group(1) );
                //** System.out.println("Quoted [" + m.group(1) + "]");
            } else
            if( m.group(2) != null) {
                items.add( m.group(2) );
                //** System.out.println("Unterminated [" + m.group(2) + "]");
            } else
            {
                items.add( m.group(3) );
                //** System.out.println("Plain [" + m.group(3) + "]");
            }
        }

        return items;
    }

    // ----------------------------------------------------------------------------

    /***
     *   Compare two strings for equality.  If both are null, then they can't ever be equal.
     *   Uses {@link String#equals(java.lang.Object) } for equality test, so case must match.
     *   This front-end handles null-references.
     *
     * <p> This method could be named {@code equals()} but then it might be confused
     *   with an {@code equal()} method for this object-type.
     *
     * @param lhs one string, or null.
     * @param rhs other string, or null.
     * @return true if same, false when different or both null-references.
     */
    public static boolean equal( String lhs, String rhs )
    {
        //  If both null, then can never be "equal".
        if( lhs == null && rhs == null )
            return false;

        if( (lhs == null && rhs != null) ||
            (lhs != null && rhs == null) )
            return false;

        //  We have two string things, so do the String.equals() compare.
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
