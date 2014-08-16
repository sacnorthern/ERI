/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

/**
 *
 * @author brian
 */
public class XmlPropertyListUtils
{

    /**
     *  Parse the propertyList XML element, with 0 or more property XML elements within. <p>
     *
     *  {@code <!ELEMENT propertyList (property*)>} <br/>
     *  {@code <!ELEMENT property (#PCDATA)>} <br/>
     *  {@code <!ATTLIST property} <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; {@code  key CDATA #REQUIRED} <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp; {@code  type  (bool|boolean|int|float|string|list|hexbytes|base64) "string" >}
     *
     * @param propListElm
     * @param propList
     * @throws SAXParseException
     */
    public static void importPropertyList( Element propListElm, AbstractXmlEntityWithPropertiesBean propList )
            throws SAXParseException
    {

        propList.clearProperties();

        NodeList   properties = propListElm.getChildNodes();
        for( int start = 0 ; start < properties.getLength() ; ++start )
        {
            Element  pr = (Element) properties.item( start );

            try {
                propList.addProperty( parsePropertyBean( pr ) );
            }
            catch( NumberFormatException ex )
            {
                throw new SAXParseException("property PCDATA ill-formed", null, ex );
            }
            catch( IllegalArgumentException ex )
            {
                throw new SAXParseException("bad property", null, ex );
            }
        }
    }

    public static XmlPropertyBean parsePropertyBean( Element propertyElm )
            throws IllegalArgumentException, NumberFormatException
    {
        String   key   = propertyElm.getAttribute( XmlPropertyBean.ATTR_KEY );
        String   type  = propertyElm.getAttribute( XmlPropertyBean.ATTR_TYPE );

        String   cdata = propertyElm.getFirstChild().getNodeValue();
        if( cdata != null )
        {
            cdata = cdata.trim();
        }

        XmlPropertyBean   pb = new XmlPropertyBean( key, type, cdata );

        return( pb );
    }
}
