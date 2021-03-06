/***  Java-ERI    Java-based Embedded Railroad Interfacing.
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

package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    @Override
    public boolean  equals(Object obj)
    {
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;

        final LayoutConfigurationBean   other = (LayoutConfigurationBean) obj;
        if( !this.m_formatVersion.equals( other.m_formatVersion ) )
            return false;
        if( !Objects.equals( this.m_bankList, other.m_bankList ) ) {
            return false;
        }
        if( !Objects.equals( this.m_layoutSensorList, other.m_layoutSensorList ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int  hashCode()
    {
        int   hc = super.hashCode();

        if( m_formatVersion != null )    hc ^= m_formatVersion.hashCode();
        if( m_bankList != null )         hc ^= m_bankList.hashCode();
        if( m_layoutSensorList != null ) hc ^= m_layoutSensorList.hashCode();

        return( hc );
    }

    @Override
    public String  toString()
    {
        StringBuilder  sb = new StringBuilder( 200 );

        sb.append( "LayoutConfigurationBean:[formatVersion=\"" );
        sb.append( m_formatVersion );

        sb.append( "\",bankList={" );
        if( m_bankList != null )
            sb.append( m_bankList.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( "},layoutSensorList={" );
        if( m_layoutSensorList != null )
            sb.append( m_layoutSensorList.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( "}]" );

        return( sb.toString() );
    }

    // ----------------------------------------------------------------------------

    /***
     *  Read an XML Layout Configuration file , that includes the different communcation
     *  banks and the inputs and outputs therein.
     *  If there is a syntax error or file-not-found, returns null.
     *
     * @param boardSpecFilename file name string, paths Oto file OK.
     * @return a layout config node holding all elements from the file, or null if an error.
     */
    public static LayoutConfigurationBean  readFromFile( String boardSpecFilename )
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

    public String  getFormatVersion()
    {
        return m_formatVersion;
    }

    public void  setFormatVersion(String formatVersion)
    {
        m_formatVersion = new String( formatVersion );
    }

    public BankListBean  getBankList()
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

    // ----------------------------------------------------------------------------

    private String      m_formatVersion;
    private BankListBean    m_bankList;
    private LayoutSensorListBean  m_layoutSensorList;
}
