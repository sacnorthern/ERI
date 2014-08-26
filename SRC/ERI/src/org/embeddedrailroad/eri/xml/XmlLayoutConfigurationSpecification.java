/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;


import com.crunchynoodles.util.FileUtils;
import com.crunchynoodles.util.XmlPropertyListBean;
import com.crunchynoodles.util.XmlPropertyListUtils;
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
 *  Start by calling {@code LayoutConfigurationBean.load(InputStream in)}. <p>
 *
 *  For issue on parsing arbitrary JSON, see https://sites.google.com/site/gson/gson-design-document
 *  All bean objects returned from parsing <em>must</em> have a parameterless constructor.
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
        if( rootElement.hasAttribute( LayoutConfigurationBean.ATTR_FORMAT_VERSION ) )
        {
            lc.setFormatVersion( rootElement.getAttribute(LayoutConfigurationBean.ATTR_FORMAT_VERSION) );
        }

        NodeList entries = rootElement.getChildNodes();
        int        start = 0;
        Element    elm;

        // <bankList> is first and required.
        elm = (Element) entries.item(start);
        if( elm.getNodeName().equals( LayoutConfigurationBean.PROP_BANK_LIST ) )
        {
            System.out.printf( "LayoutConfigurationBean child #%d is \"%s\"\n", start, elm.getNodeName() );

            /**  <!ELEMENT bankList (bank*)>  **/
            lc.setBankList( importBankList( elm ) );
            ++start;
        }

        // <layoutSensorList> is next and required.
        elm = (Element) entries.item(start);
        if( elm.getNodeName().equals( LayoutConfigurationBean.PROP_LAYOUT_SENSOR_LIST ) )
        {
            System.out.printf( "LayoutConfigurationBean child #%d is \"%s\"\n", start, elm.getNodeName() );

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
        for( int start = 0 ; start < entries.getLength() ; ++start )
        {
            elm = (Element) entries.item(start);
            if( elm.getNodeName().equals( BankListBean.PROP_BANK ) )
            {
                System.out.printf( "BankListBean child #%d is \"%s\"\n", start, elm.getNodeName() );

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

        //    <!ELEMENT bank (network,unit*)>
        //    <!ATTLIST bank
        //            protocol   CDATA    #REQUIRED
        //            address    CDATA    #REQUIRED
        //            alias      CDATA    "" >

        if( bankElm.hasAttribute( BankBean.ATTR_PROTOCOL ) )
        {
            bb.setProtocol( bankElm.getAttribute(BankBean.ATTR_PROTOCOL) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + BankBean.PROP_ELEMENT_NAME + "\" missing its \"" + BankBean.ATTR_PROTOCOL + "\" attribute", null );
        }

        if( bankElm.hasAttribute( BankBean.ATTR_ADDRESS ) )
        {
            bb.setAddress( bankElm.getAttribute(BankBean.ATTR_ADDRESS) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + BankBean.PROP_ELEMENT_NAME + "\" missing its \"" + BankBean.ATTR_ADDRESS + "\" attribute", null );
        }

        if( bankElm.hasAttribute( BankBean.ATTR_ALIAS ) )
        {
            bb.setAlias( bankElm.getAttribute(BankBean.ATTR_ALIAS) );
        }

        NodeList entries = bankElm.getChildNodes();
        Element    elm;
        int     start = 0;

        // <network> is first.
        elm = (Element) entries.item(start);
        if( elm != null && elm.getNodeName().equals( NetworkBean.PROP_ELEMENT_NAME ) )
        {
            System.out.printf( "BankBean child #%d is \"%s\"\n", start, elm.getNodeName() );

            /**  <!ELEMENT bank (network,...)> **/
            bb.setNetwork( importNetwork( elm ) );
            ++start;
        }
        else
        {
             throw new SAXParseException( BankBean.PROP_ELEMENT_NAME + " is missing an " + NetworkBean.PROP_ELEMENT_NAME + " element", null );
        }

        // <unit> is second and is a sequence of 0 or more elements.
        //  TODO: Should probably be <unitList> <unit> ... </unit> <unit> ... </unit> </unitList>
        for( ; start < entries.getLength() ; ++start )
        {
            elm = (Element) entries.item(start);
            if( elm.getNodeName().equals( BankBean.ELEMENT_UNIT ) )
            {
                System.out.printf( "BankBean child #%d is \"%s\"\n", start, elm.getNodeName() );

                /**  <!ELEMENT bank (...,unit*)> **/
                bb.addUnit( importUnit( elm ) );
            }
            else
            {
                throw new SAXParseException( "Invalid sub-element of \"" + BankBean.PROP_ELEMENT_NAME + "\" : " + elm.getNodeName(), null );
            }
        }

        return( bb );
    }

    // ----------------------------------------------------------------------------

    static NetworkBean importNetwork( Element commsElm )
            throws SAXParseException
    {
        NetworkBean   cb = new NetworkBean();

        /*** <!ELEMENT network (propertyList)> */
        /*** <!ATTLIST network enabled  %Boolean;   "yes">  */

        boolean  en = XmlUtils.ParseBooleanAttribute( commsElm, NetworkBean.ATTR_ENABLED, true );
        cb.setEnabled( en );

        String  propListName = (new XmlPropertyListBean()).getElementName();

        NodeList  children = commsElm.getChildNodes();
        if( children.getLength() != 1 )
        {
            throw new SAXParseException( NetworkBean.PROP_ELEMENT_NAME + " must have one " +
                        propListName + " child element", null );
        }

        Element plist = (Element) children.item( 0 );
        if( ! plist.getTagName().equalsIgnoreCase( propListName ) )
        {
            throw new SAXParseException( NetworkBean.PROP_ELEMENT_NAME + " must have one " +
                        propListName + " child element", null );
        }

        //  Add properties to network-bean directly.
        XmlPropertyListUtils.importPropertyList( plist, cb );

        return( cb );
    }

    // ----------------------------------------------------------------------------

    static UnitBean importUnit( Element unitElm )
            throws SAXParseException
    {
        UnitBean  unit = new UnitBean();

        //  <!ELEMENT unit (propertyList,inputgroup*,outputgroup*)>
        //  <!ATTLIST unit
        //          address CDATA   #REQUIRED
        //          type    CDATA   #REQUIRED
        //          alias   CDATA   ""
        //          protocol CDATA  "1"

        if( unitElm.hasAttribute( UnitBean.ATTR_ADDRESS ) )
        {
            unit.setAddress( unitElm.getAttribute(UnitBean.ATTR_ADDRESS) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + UnitBean.PROP_ELEMENT_NAME + "\" missing its \"" + UnitBean.ATTR_ADDRESS + "\" attribute", null );
        }

        if( unitElm.hasAttribute( UnitBean.ATTR_TYPE ) )
        {
            unit.setType( unitElm.getAttribute(UnitBean.ATTR_TYPE) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + UnitBean.PROP_ELEMENT_NAME + "\" missing its \"" + UnitBean.ATTR_TYPE + "\" attribute", null );
        }

        //  Check the optional attributes.  The UnitBean creates itself with default values for these.
        if( unitElm.hasAttribute( UnitBean.ATTR_ALIAS ) )
        {
            unit.setAlias(unitElm.getAttribute(UnitBean.ATTR_ALIAS) );
        }

        if( unitElm.hasAttribute( UnitBean.ATTR_PROTOCOL ) )
        {
            unit.setProtocol(unitElm.getAttribute(UnitBean.ATTR_PROTOCOL) );
        }

        //  Do the <propertyList> group first.
        String  propListName = (new XmlPropertyListBean()).getElementName();

        NodeList  children = unitElm.getChildNodes();
        if( children.getLength() == 0 )
        {
            throw new SAXParseException( UnitBean.PROP_ELEMENT_NAME + " missing its " +
                        propListName + " child element", null );
        }

        Element plist = (Element) children.item( 0 );
        if( ! plist.getTagName().equalsIgnoreCase( propListName ) )
        {
            throw new SAXParseException( NetworkBean.PROP_ELEMENT_NAME + " must have one " +
                        propListName + " child element", null );
        }

        //  Add properties to unit-bean directly.
        XmlPropertyListUtils.importPropertyList( plist, unit );

        //  Collect the sub-elements <inputgroup ... />, <outputgroup ... />

        Element  iogroup;
        AbstractInputOutputGroup  absIo;

        for( int start = 1 ; start < children.getLength() ; ++start )
        {
            iogroup = (Element) children.item( start );
            String  tag_name = iogroup.getTagName();
            System.out.printf( "   unit child #%d is %s", start, tag_name );

            //  Create inputGroup and outputGroup objects in anticipation.
            //  Also, the #getElementName() method returns different values for
            //  each class-type.  Since Java doesn't have class-type interfaces,
            //  we create the two objects and then ask them what they are named.
            //
            //  Only one of the "group" objects is filled with attributes ; the other
            //  is left to wilt .....

            InputGroupBean  inbean = new InputGroupBean();
            OutputGroupBean  outbean = new OutputGroupBean();

            if( tag_name.equals( inbean.getElementName() ) )
            {
                parseInputOutputGroupAttrs( iogroup, tag_name, inbean );
                absIo = inbean;
                outbean = null;
            }
            else
            if( tag_name.equals( outbean.getElementName() ) )
            {
                parseInputOutputGroupAttrs( iogroup, tag_name, outbean );
                absIo = outbean;
                inbean = null;
            }
            else
            {
                throw new SAXParseException( UnitBean.PROP_ELEMENT_NAME + " has unknown sub-element \"" +
                        tag_name + "\"", null );
            }

            System.out.printf( " ... first=%s, last=%s\n", absIo.getFirst(), absIo.getLast() );

            unit.addInputOutputGroup( absIo );
        }

        return( unit );
    }

    /***
     *  Parse all attributes of an inputGroup or outputGroup element.
     *  Attributes are same for these two kinds, and all attributes are required.
     *
     * @param ioElm XML element to examine for attributes
     * @param tag_name Name of this element (cuz caller already obtained it)
     * @param group XML bean to update
     * @throws SAXParseException If an attribute is missing
     */
    static void parseInputOutputGroupAttrs( Element ioElm, String  tag_name, AbstractInputOutputGroup group )
            throws SAXParseException
    {

        if( ioElm.hasAttribute( AbstractInputOutputGroup.ATTR_FIRST ) )
        {
            group.setFirst( ioElm.getAttribute( AbstractInputOutputGroup.ATTR_FIRST ) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + group.getElementName() + "\" missing its \"" + AbstractInputOutputGroup.ATTR_FIRST + "\" attribute", null );
        }

        if( ioElm.hasAttribute( AbstractInputOutputGroup.ATTR_LAST ) )
        {
            group.setLast( ioElm.getAttribute( AbstractInputOutputGroup.ATTR_LAST ) );
        }
        else
        {
            throw new SAXParseException( "Element \"" + group.getElementName() + "\" missing its \"" + AbstractInputOutputGroup.ATTR_LAST + "\" attribute", null );
        }

    }

    // ========================================================================

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
            System.out.printf( "Error on line #%d (inner)\n", x.getLineNumber() );
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
