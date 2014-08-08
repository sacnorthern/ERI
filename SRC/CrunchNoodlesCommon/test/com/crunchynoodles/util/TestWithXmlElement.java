/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import java.util.HashMap;
import java.util.Map;
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
class TestWithXmlElement implements Element
{

    public TestWithXmlElement( String name )
    {
        m_name = name;
        m_attrs = new HashMap<String,String >();
        m_cdata = "";
    }

    @Override
    public String getTagName() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAttribute( String name ) {
        // "The Attr value as a string, or the empty string if that attribute does not have a specified or default value."
        for( Map.Entry<String,String > en : m_attrs.entrySet() )
        {
            if( en.getKey().equals( name ) )
            {
                return en.getValue();
            }
        }
        return "";
    }

    @Override
    public void setAttribute( String name, String value )
            throws DOMException {
        removeAttribute( name );
        m_attrs.put( name, value );
    }

    @Override
    public void removeAttribute( String name )
            throws DOMException {
        m_attrs.remove( name );

        //   *** NOT IMPLEMENTED ***
        //  "If a default value for the removed attribute is defined in the DTD, a new attribute
        //   immediately appears with the default value as well as the corresponding namespace URI,
        //   local name, and prefix when applicable."

    }

    @Override
    public Attr getAttributeNode( String name ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attr setAttributeNode( Attr newAttr )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attr removeAttributeNode( Attr oldAttr )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeList getElementsByTagName( String name ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAttributeNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttributeNS( String namespaceURI, String qualifiedName, String value )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAttributeNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attr getAttributeNodeNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attr setAttributeNodeNS( Attr newAttr )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeList getElementsByTagNameNS( String namespaceURI, String localName )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasAttribute( String name ) {
        return m_attrs.containsKey( name );
    }

    @Override
    public boolean hasAttributeNS( String namespaceURI, String localName )
            throws DOMException {
        // not implemented, so don't have any.
        return false;
        // throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIdAttribute( String name, boolean isId )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIdAttributeNS( String namespaceURI, String localName, boolean isId )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIdAttributeNode( Attr idAttr, boolean isId )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNodeName() {
        return m_name;
    }

    @Override
    public String getNodeValue()
            throws DOMException {
        return m_cdata;
    }

    @Override
    public void setNodeValue( String nodeValue )
            throws DOMException {
        m_cdata = new String( nodeValue );
    }

    @Override
    public short getNodeType() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getParentNode() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeList getChildNodes() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getFirstChild() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getLastChild() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getPreviousSibling() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getNextSibling() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NamedNodeMap getAttributes() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document getOwnerDocument() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node insertBefore( Node newChild, Node refChild )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node replaceChild( Node newChild, Node oldChild )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node removeChild( Node oldChild )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node appendChild( Node newChild )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasChildNodes() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node cloneNode( boolean deep ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void normalize() {
        // do nothing.
        // throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSupported( String feature, String version ) {
        return false;
        // throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNamespaceURI() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPrefix() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrefix( String prefix )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasAttributes() {
        return ! m_attrs.isEmpty();
    }

    @Override
    public String getBaseURI() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short compareDocumentPosition( Node other )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTextContent()
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTextContent( String textContent )
            throws DOMException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSameNode( Node other ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String lookupPrefix( String namespaceURI ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDefaultNamespace( String namespaceURI ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String lookupNamespaceURI( String prefix ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEqualNode( Node arg ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getFeature( String feature, String version ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object setUserData( String key, Object data, UserDataHandler handler ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getUserData( String key ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    // ----------------------------------------------------------------------------

    String  m_name;

    String  m_cdata;

    HashMap<String, String>   m_attrs;
}
