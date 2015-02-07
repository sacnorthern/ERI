/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
 ***
 ***  Licensed under the Apache License, Version 2.0 ( the "License" ) ;
 ***  you may not use this file except in compliance with the License.
 ***  You may obtain a copy of the License at:
 ***        http://www.apache.org/licenses/LICENSE-2.0
 ***
 ***  Unless required by applicable law or agreed to in writing, software
 ***  distributed under the License is distributed on an "AS IS" BASIS,
 ***  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

import java.util.logging.Logger;
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
     * @param propListElm propertyList element, with little property elements as PCDATA.
     * @param propList Where to collect/put the property elements.
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
                throw new SAXParseException( "property PCDATA ill-formed", null, ex );
            }
            catch( IllegalArgumentException ex )
            {
                throw new SAXParseException( "bad property", null, ex );
            }
        }
    }

    /***
     *  Parse an XML property element, with data conversion of cdata to 'type'.
     *
     * @param propertyElm element with tags {@link XmlPropertyBean#ATTR_KEY} and {@link XmlPropertyBean#ATTR_TYPE}.
     * @return {@link XmlPropertyBean} holding key, type-identification, and some data.
     * @throws IllegalArgumentException from {@code new XmlPropertyBean()}
     * @throws NumberFormatException Can't convert CDATA into type.
     */
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

        logger.info( "XML parsing  " + type + ": " + key + " = \"" + (cdata != null ? cdata : "(nothing)" ) + "\"" );

        XmlPropertyBean   pb = new XmlPropertyBean( key, type, cdata );

        return( pb );
    }

    // ----------------------------------------------------------------------------

    private static final Logger     logger = Logger.getLogger( XmlPropertyListUtils.class.getName() );

}
