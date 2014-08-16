/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.AbstractXmlEntityWithPropertiesBean;
import java.util.Arrays;
import java.util.List;
import sun.nio.cs.ext.ExtendedCharsets;

/**
 *
 * @author brian
 */
public class UnitBean
    extends AbstractXmlEntityWithPropertiesBean
{
    public static final String PROP_ELEMENT_NAME = "unit";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_ADDRESS, ATTR_TYPE, ATTR_PROTOCOL, ATTR_ALIAS );
    }

    public UnitBean()
    {
        super();
    }

    // ----------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int  h = super.hashCode();

        if( null != m_address )    h ^= m_address.hashCode();
        if( null != m_type )       h ^= m_type.hashCode();
        if( null != m_protocol )   h ^= m_protocol.hashCode();
        if( null != m_alias )      h ^= m_alias.hashCode();

        return( h );
    }

    @Override
    public String toString()
    {
        StringBuilder  sb = new StringBuilder( 80 );

        sb.append( "UnitBean:[address=" );
        sb.append( m_address );
        sb.append( ",type=\"" );
        sb.append( m_type );
        sb.append( "\",protocol=\"" );
        sb.append( m_protocol );
        sb.append( "\",alias=\"" );
        sb.append( m_alias );
        sb.append( '"' );

        sb.append( ",m_io_groups=" );
        if( m_io_groups != null )
            sb.append( '{' ).append( m_io_groups.toString() ).append( '}' );
        else
            sb.append( NULL_OBJECT_REF_STRING );
        sb.append( ',' );

        sb.append( '{' ).append( super.toString() ).append( '}' );

        return( sb.toString() );
    }

    // ----------------------------------------------------------------------------

    public static final String  ATTR_ADDRESS = "address";
    protected String    m_address;

    public String   getAddress()
    {
        return m_address;
    }

    public void     setAddress( String addr )
    {
        m_address = addr;
    }

    public static final String  ATTR_TYPE = "type";
    protected String    m_type;

    public String   getType()
    {
        return m_type;
    }

    public void     setType( String typestr )
    {
        m_type = typestr;
    }

    public static final String  ATTR_PROTOCOL = "protocol";
    protected String    m_protocol;

    public String   getProtocol()
    {
        return m_protocol;
    }

    public void     setProtocol( String proto )
    {
        m_protocol = proto;
    }

    public static final String  ATTR_ALIAS = "alias";
    protected String    m_alias;

    public String   getAlias()
    {
        return m_alias;
    }

    public void     setAlias( String alias )
    {
        m_alias = alias;
    }

}
