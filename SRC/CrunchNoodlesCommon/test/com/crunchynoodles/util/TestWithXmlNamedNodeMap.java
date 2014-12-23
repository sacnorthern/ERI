/***  This file is dedicated to the public domain, 2014 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author brian
 */
public class TestWithXmlNamedNodeMap implements NamedNodeMap
{

    @Override
    public Node getNamedItem( String name ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node setNamedItem( Node arg )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node removeNamedItem( String name )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node item( int index ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLength() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getNamedItemNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node setNamedItemNS( Node arg )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node removeNamedItemNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    // ----------------------------------------------------------------------------


}
