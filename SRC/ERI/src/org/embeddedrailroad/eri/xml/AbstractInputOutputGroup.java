/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;

/**
 *  Common code for XML elements <b>inputGroup</b> and <b>outputGroup</b>, which includes
 *  first and last pin identifiers (strings).
 *  Sub-class constructors set the input-ness and/or output-ness of the XML element, so
 *  no constructor in this abstract class!! <p>
 *
 *  The interpretation of the first and last values is up to the bank's protocol.
 *  E.g. if talking to an ARM CPU with lettered ports, first could be "PA.3" (port "A", pin 3 ) and
 *  last could be "PA.31" (port "A", pin 31).
 *  Parsing and using is up to the bank object.
 *
 * @author brian
 */
public abstract class AbstractInputOutputGroup
    implements XmlEntityBean
{

    // ----------------------------------------------------------------------------

    @Override
    public int  hashCode()
    {
        int  hc = super.hashCode();

        if( m_is_input )        hc ^= (int) 'I';
        if( m_is_output )       hc ^= (int) 'O';
        if( m_first != null )   hc ^= m_first.hashCode();
        if( m_last != null )    hc ^= m_last.hashCode();

        return( hc );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final AbstractInputOutputGroup other = (AbstractInputOutputGroup) obj;
        if( this.m_is_input != other.m_is_input ) {
            return false;
        }
        if( this.m_is_output != other.m_is_output ) {
            return false;
        }
        if( ! m_first.equals( other.m_first ) )
            return false;
        if( ! m_last.equals( other.m_last ) )
            return false;

        return true;
    }

    @Override
    public String  toString()
    {
        StringBuilder  sb = new StringBuilder( 50 );

        sb.append( this.getElementName() );
        sb.append( ":[" );
        sb.append( "first=\"" );
        sb.append( m_first );
        sb.append( "\",last=\"" );
        sb.append( m_last );
        sb.append( '"' );

        // sb.append( ",super={" ).append( super.toString() ).append( '}' );

        sb.append( ']' );

        return( sb.toString() );
    }

    // ----------------------------------------------------------------------------

    public static final String ATTR_FIRST = "first";
    protected String         m_first;

    public String getFirst()
    {
        return m_first;
    }

    public void   setFirst( String firstCode )
    {
        m_first = firstCode;
    }

    // ----------------------------------------------------------------------------

    public static final String ATTR_LAST = "last";
    protected String         m_last;

    public String getLast()
    {
        return m_last;
    }

    public void   setLast( String lastCode )
    {
        m_last = lastCode;
    }

    // ----------------------------------------------------------------------------

    protected boolean       m_is_input;

    public boolean  isInput()
    {
        return m_is_input;
    }

    protected boolean       m_is_output;

    public boolean  isOutput()
    {
        return m_is_output;
    }

}
