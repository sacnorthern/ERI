/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;


import com.crunchynoodles.util.FileUtils;
import com.crunchynoodles.util.XmlUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *  Provide the direct XML interface to the ERI Layout Configuration specification data file.
 *  This supports only reading. <p>
 *
 *  Start by calling {@code LayoutConfigurationBean.load(InputStream in)}.
 *
 * @author brian
 * @see java.util.XMLUtils
 * @see LayoutConfigurationBean
 * @see http://oak.cs.ucla.edu/cs144/reference/DTD.html
 */
public class XmlLayoutConfigurationSpecification
{
    // The required DTD URI for exported properties
    public static final String     LAYOUT_CONFIG_SPEC_DTD_URI =
                "http://embeddedrailroad.org/eri/dtd/layoutconfiguationspecification.dtd";

    private static String LAYOUT_CONFIG_SPEC_DTD;

    private static String _getDTD()
    {
        if( LAYOUT_CONFIG_SPEC_DTD == null )
        {
            try
            {
                LAYOUT_CONFIG_SPEC_DTD = FileUtils.readWholeFile( "layout_config_spec.dtd", "UTF-8" );
                // System.out.printf( "\n--- just read ---\n%s\n--- just read ---\n", LAYOUT_CONFIG_SPEC_DTD );
                // System.out.flush();
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                System.exit( -2 );
            }
        }

        return LAYOUT_CONFIG_SPEC_DTD;
    }

    // ----------------------------------------------------------------------------

    /***
     *  Load a whole layout specification XML data file.
     *  Throws exceptions on failures.
     *
     * @param in input stream to read from.
     * @return LayoutConfigurationBean with complete data.
     *
     * @throws IOException Troubles reading XML file
     * @throws InvalidPropertiesFormatException Troubles parsing the XML file.
     * @throws SAXParseException Can't convert NMTOKEN into a number.
     */
    static LayoutConfigurationBean load(InputStream in)
        throws IOException, SAXParseException, InvalidPropertiesFormatException
    {
        Document doc;

        try {
            doc = getLoadingDoc(in);
        }
        catch (SAXException saxe) {
            throw new InvalidPropertiesFormatException(saxe);
        }

        Element rootElement = doc.getDocumentElement();

        return importBoardSpecification( rootElement );
    }

    // ----------------------------------------------------------------------------

    static LayoutConfigurationBean importBoardSpecification( Element rootElement )
            throws SAXParseException
    {
        LayoutConfigurationBean  lc = new LayoutConfigurationBean();

        /***  <!ELEMENT layoutSpecification (bankList,layoutSensorList) >   ***/
        /***  <!ATTLIST layoutSpecification formatVersion CDATA #REQUIRED > ***/

        // Take attributes of <layoutSpecification ...> first.
        if( rootElement.hasAttribute( LayoutConfigurationBean.PROP_FORMAT_VERSION ) )
        {
            lc.setFormatVersion( rootElement.getAttribute(LayoutConfigurationBean.PROP_FORMAT_VERSION) );
        }

        NodeList entries = rootElement.getChildNodes();
        int        start = 0;
        Element    elm;

        // <bankList> is first and required.
        elm = (Element) entries.item(start);
        if( elm.getNodeName().equals( LayoutConfigurationBean.PROP_BANK_LIST ) )
        {
            System.out.printf( "child #%d is \"%s\"\n", start, elm.getNodeName() );

            /**  <!ELEMENT bankList (bank*)>  **/
            lc.setBankList( importBankList( elm ) );
            ++start;
        }

        // <layoutSensorList> is next and required.
        elm = (Element) entries.item(start);
        if( elm.getNodeName().equals( LayoutConfigurationBean.PROP_LAYOUT_SENSOR_LIST ) )
        {
            System.out.printf( "child #%d is \"%s\"\n", start, elm.getNodeName() );

            /**  <!ELEMENT layoutSensorList (layoutSensor*)>  **/
            lc.setLayoutSensorList( importLayoutSensorList( elm ) );
            ++start;
        }

        return( lc );
    }

    // ----------------------------------------------------------------------------

    static BankListBean importBankList( Element bankListElm )
            throws SAXParseException
    {
        BankListBean    bl = new BankListBean();
        List<BankBean>  bank_list = new ArrayList<>();

        /***  <!ELEMENT bankList (bank*)>   ***/

        NodeList entries = bankListElm.getChildNodes();
        Element    elm;

        // <bank> is first and is a sequence of 0 or more elements.
        for( int start = 0 ; entries.getLength() < start ; ++start )
        {
            elm = (Element) entries.item(start);
            if( elm.getNodeName().equals( BankListBean.PROP_BANK ) )
            {
                System.out.printf( "child #%d is \"%s\"\n", start, elm.getNodeName() );

                /**  <!ELEMENT bank (comms unit*)>  **/
                bank_list.add( importBank( elm ) );
            }
            else
            {
                throw new SAXParseException( "Invalid sub-element of \"bankList\" : " + elm.getNodeName(), null );
            }

        }

        bl.setBankList( bank_list );

        return( bl );
    }

    static BankBean importBank( Element bankElm )
            throws SAXParseException
    {
        BankBean    bb = new BankBean();

        //    <!ELEMENT bank (comms,unit*)>
        //    <!ATTLIST bank
        //            protocol   NMTOKEN  #REQUIRED
        //            address    NMTOKEN  #REQUIRED
        //            alias      CDATA    "" >

        if( bankElm.hasAttribute( BankBean.PROP_PROTOCOL ) )
        {
            bb.setProtocol(bankElm.getAttribute(BankBean.PROP_PROTOCOL) );
        }
        else
        {
            throw new SAXParseException( "Element \"bank\" missing its \"" + BankBean.PROP_PROTOCOL + "\" attribute", null );
        }

        if( bankElm.hasAttribute( BankBean.PROP_ADDRESS ) )
        {
            bb.setAddress( bankElm.getAttribute(BankBean.PROP_ADDRESS) );
        }
        else
        {
            throw new SAXParseException( "Element \"bank\" missing its \"" + BankBean.PROP_ADDRESS + "\" attribute", null );
        }

        if( bankElm.hasAttribute( BankBean.PROP_ALIAS ) )
        {
            bb.setAlias( bankElm.getAttribute(BankBean.PROP_ALIAS) );
        }


        return( bb );
    }


    // ----------------------------------------------------------------------------

    static LayoutSensorListBean importLayoutSensorList( Element LayoutSensorElm )
    {
        return null;
    }

    // ========================================================================
    // ========================================================================

    static Document getLoadingDoc(InputStream in)
        throws SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(true);
        dbf.setCoalescing(true);
        dbf.setIgnoringComments(true);

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new XmlLayoutConfigurationSpecification.Resolver());
            db.setErrorHandler(new XmlLayoutConfigurationSpecification.EH());
            InputSource is = new InputSource(in);
            return db.parse(is);
        } catch (ParserConfigurationException x) {
            throw new Error(x);
        }
    }

    private static class Resolver implements EntityResolver
    {
        /***
         *  If the DTD's URI reference is what we understand, then create a reader
         *  to get the DTD ( contains parsing rules ).
         *  If not matching {@code sid}, then throw {@code SAXException}
         *
         * @return Reader for DTD
         */
        @Override
        public InputSource resolveEntity(String pid, String sid)
            throws SAXException
        {
            if (sid.equals(LAYOUT_CONFIG_SPEC_DTD_URI))
            {
                InputSource is;
                _getDTD();
                is = new InputSource(new StringReader(LAYOUT_CONFIG_SPEC_DTD));
                is.setSystemId(LAYOUT_CONFIG_SPEC_DTD_URI);
                return is;
            }
            throw new SAXException("Invalid system identifier: " + sid);
        }
    }

    private static class EH implements ErrorHandler
    {
        @Override
        public void error(SAXParseException x) throws SAXException {
            throw x;
        }
        @Override
        public void fatalError(SAXParseException x) throws SAXException {
            throw x;
        }
        @Override
        public void warning(SAXParseException x) throws SAXException {
            throw x;
        }
    }

    private static final Logger logger = Logger.getLogger( XmlLayoutConfigurationSpecification.class.getName() );

}
