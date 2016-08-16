/***  This file is dedicated to the public domain, 2014, 2016 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

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
        final String  PREFIX = "";
        final String  URI = "";

        for( Map.Entry<String,String>  e : def_attrs.entrySet() )
        {
            TestWithAttr  a = new TestWithAttr( this, PREFIX, e.getKey(), e.getKey(), URI, e.getValue() );
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

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException
    {
        //  Use introspection to find 'Attr[] attrs' in parent.  This field is
        //  package-only.  This test class is a leach, so pry a look-see.

        final String  ATTRS_FIELD_NAME = "attrs";
        Field  f = null;

        Class  cl = this.getClass();
        do
        {
            try {
                f = cl.getDeclaredField( ATTRS_FIELD_NAME );
            }
            catch( NoSuchFieldException ex ) {
                //  field not found, so go up to superclass.
                cl = cl.getSuperclass();
                continue;
            }
        }
        while( null != cl && null == f );

        //  If no field 'attrs' , then cannot add attribute.  :(
        if( null == f )
            return( null );

        boolean  hidden = false;
        try {
            int  orig_len = 0;
            Attr[]   updated_attrs;
            hidden = ! f.isAccessible();

            if( hidden )
                f.setAccessible( true );

            //  Now unlocked, so get array.
            Attr[]   supers_attr = (Attr[]) f.get( this );

            //  make room.
            if( null == supers_attr )
            {
                //  Create list and insert first element.
                updated_attrs = new Attr[1];
            }
            else
            {
                orig_len = supers_attr.length;
                updated_attrs = Arrays.copyOf( supers_attr, orig_len + 1 );

            }

            // append new attribute.
            updated_attrs[ orig_len ] = newAttr;

            //  Update instance variable.
            f.set( this, updated_attrs );

        }
        catch( IllegalAccessException ex ) {
            System.err.printf( "%s:setAttributeNode() failed\n%s", this.getClass().getName(), ex.toString() );
            return( null );
        }
        finally {
            if( hidden )
                f.setAccessible( false );
        }

        return newAttr;
    }

    // ----------------------------------------------------------------------------

}
