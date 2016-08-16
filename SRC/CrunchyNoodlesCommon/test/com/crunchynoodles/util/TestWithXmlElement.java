/***  This file is dedicated to the public domain, 2014, 2016 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;

/**
 *
 * @author brian
 */
class TestWithXmlElement extends ElementImpl
{

    public TestWithXmlElement(int line, int column, int offset)
    {
        super( line, column, offset );
    }

    public TestWithXmlElement(int line, int column)
    {
        super( line, column );
    }

    public TestWithXmlElement(String prefix, String localpart, String rawname,
                                String uri, int line, int column, int offset)
    {
        super( prefix, localpart, rawname, uri, line, column, offset );
    }

    public TestWithXmlElement(String prefix, String localpart, String rawname,
                                String uri, int line, int column)
    {
        super( prefix, localpart, rawname, uri, line, column );
    }

    /***
     *  Specific constructor to make element with our sample attributes.
     */
    public TestWithXmlElement( String localpart )
    {
        super( "", localpart, localpart, "", s_counter, 0 );
        ++s_counter;

        myElementInit();
    }

    private static int     s_counter = 1;

    // ----------------------------------------------------------------------

    private void myElementInit()
    {

        //  Create defaults.
        //  "The Double Brace Initialization Idiom firsts appears as very appealing. ...  when you
        //   declare a class with double braces, an anonymous class will be created. Because the
        //   anonymous class is not static, it will also hold a reference (this$0) to its containing
        //   class (if defined within another class, which is the most normal case). This means that
        //   the containing instance (this$0) can not be garbage-collected as long as your Map is alive."
        //      ( from http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html )
        //
        Map<String, String>   def_attrs = imperative();

        for( Map.Entry<String,String>  e : def_attrs.entrySet() )
        {
            TestWithAttr  a = new TestWithAttr( this, "", e.getKey(), e.getKey(), "", e.getValue() );
            this.setAttributeNode( a );
        }

    }

    /*** Hashmap with default values from our DTD.
     *   ( from http://minborgsjavapot.blogspot.com/2014/12/java-8-initializing-maps-in-smartest-way.html )
     */

    protected static Map< String, String > imperative()
    {
        final Map< String, String > def_map = new HashMap<>();
        def_map.put( "lang", "en" );
        def_map.put( "input", "false" );
        def_map.put( "output", "false" );
        def_map.put( "whole", "false" );
        return ( def_map );
        //!! return Collections.unmodifiableMap(def_map);
    }

    // ----------------------------------------------------------------------


// ----------------------------------------------------------------------------

}
