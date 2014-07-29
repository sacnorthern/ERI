/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.xml.sax.SAXParseException;

/**
 *  Represents all the XML data read from the board specification file.
 *  Built as a bean.
 *
 * <p> {@code <!ELEMENT layoutSpecification (bankList,layoutSensorList)>} <br/>
 * {@code <!ATTLIST layoutSpecification formatVersion CDATA #REQUIRED>}
 *
 * @author brian
 */
public class LayoutConfigurationBean
    implements XmlEntityBean
{
    public static final String PROP_ELEMENT_NAME = "layoutConfiguration";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_FORMAT_VERSION );
    }

    public LayoutConfigurationBean()
    {
    }

    // ----------------------------------------------------------------------------

    public static LayoutConfigurationBean readFrom( String boardSpecFilename )
    {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream( boardSpecFilename );
            return XmlLayoutConfigurationSpecification.load( ins );
        }
        catch( IOException ex ) {
            System.out.println( "I/O Error!!" );
            ex.printStackTrace();
            return null;
        } catch( SAXParseException ex ) {
            System.out.printf( "File trouble, line #%d (outer)\n", ex.getLineNumber() );
            ex.printStackTrace();
            return null;
        }
        finally {
            if( ins != null )
                try { ins.close(); } catch( Exception ex ) { }
        }
    }

    // ----------------------------------------------------------------------------

    public static final String  ATTR_FORMAT_VERSION     = "formatVersion";  // atttribute
    public static final String  PROP_BANK_LIST          = "bankList";       // element within
    public static final String  PROP_LAYOUT_SENSOR_LIST = "layoutSensorList"; // element within

    public String getFormatVersion()
    {
        return m_formatVersion;
    }

    public void setFormatVersion(String formatVersion)
    {
        m_formatVersion = new String( formatVersion );
    }

    public BankListBean getBankList()
    {
        return m_bankList;
    }

    public void setBankList( BankListBean banks )
    {
        m_bankList = banks;
    }

    public LayoutSensorListBean getLayoutSensorList()
    {
        return m_layoutSensorList;
    }

    public void setLayoutSensorList( LayoutSensorListBean layout_sensors )
    {
        m_layoutSensorList = layout_sensors;
    }

    private String      m_formatVersion;
    private BankListBean    m_bankList;
    private LayoutSensorListBean  m_layoutSensorList;
}
