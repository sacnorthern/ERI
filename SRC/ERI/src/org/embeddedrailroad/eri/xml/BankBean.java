/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/***
 *  A "bank" is one communication protocol talking with a series of "unit" things.
 *  There may be several banks with the same protocol.
 *  All units on bank must have a unique address, though any may receive broadcast messages. <br>
 *
 *  <pre> &lt;!ELEMENT bank (comms,unit*)&gt;
 * &lt;!ATTLIST bank
 *              protocol   CDATA  #REQUIRED
 *              address    CDATA  #REQUIRED
 *              alias      CDATA  ""  &gt;</pre>
 *
 * @author brian
 */
public class BankBean
    implements XmlEntityBean
{
    public static final String PROP_ELEMENT_NAME = "bankList";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_PROTOCOL, ATTR_ADDRESS, ATTR_ALIAS );
    }

    public BankBean()
    {
        m_alias = "";
    }

    // ----------------------------------------------------------------------------

    @Override
    public int  hashCode()
    {
        int  hc = super.hashCode();

        if( m_comms_element != null )   hc ^= m_comms_element.hashCode();
        if( m_protocol != null )        hc ^= m_protocol.hashCode();
        if( m_address != null )         hc ^= m_address.hashCode();
        if( m_alias != null )           hc ^= m_alias.hashCode();
        if( m_unit_element != null )    hc ^= m_unit_element.hashCode();

        return( hc );
    }

    @Override
    public String  toString()
    {
        StringBuilder  sb = new StringBuilder( 100 );

        sb.append( "BankBean:[" );

        sb.append( "m_comms_element=" );
        if( m_comms_element != null )
            sb.append( m_comms_element.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( ",m_protocol=" );
        if( m_protocol != null )
            sb.append( m_protocol.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( ",m_address=" );
        if( m_address != null )
            sb.append( m_address.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append(  ",alias=" );
        if( m_alias != null )
        {
            sb.append( '"' ).append( m_alias.toString() ).append( '"' );
        }
        else
        {
            sb.append( NULL_OBJECT_REF_STRING );
        }

        sb.append( ",m_unit_element={" );
        if( m_unit_element != null )
            sb.append( m_unit_element.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( "}]" );

        return( sb.toString() );
    }

    // ----------------------------------------------------------------------------

    public static final String ELEMENT_COMMS = "comms";
    protected CommsBean         m_comms_element;

    public CommsBean getComms()
    {
        return m_comms_element;
    }

    public void     setComms( CommsBean comms_ele )
    {
        m_comms_element = comms_ele;
    }

    // ----------------------------------------------------------------------------

    public static final String ELEMENT_UNIT = "unit";
    protected List<UnitBean>          m_unit_element = new ArrayList<UnitBean>();

    /***
     *  Return known units in an array, order is arbitrary.
     * @return array with references to units held by BankBean.
     */
    public UnitBean[]  getUnits()
    {
        return (UnitBean[]) m_unit_element.toArray();
    }

    /***
     *  Create a modern Java typed iterator with reference to units held by BankBean.
     * @return Iterator over Unit beans.
     */
    public Iterator<UnitBean> getUnitIterator()
    {
        return m_unit_element.iterator();
    }

    /***
     *  Add another Unit bean to known units.
     *  Caller's object is stored, not copied, not shaken, and not stirred.
     *
     * @param unit_ele Another unit.
     */
    public void     addUnit( UnitBean unit_ele )
    {
        m_unit_element.add( unit_ele );
    }

    // ----------------------------------------------------------------------------

    public static final String ATTR_PROTOCOL   = "protocol";    // attribute
    public static final String ATTR_ADDRESS    = "address";     // attribute
    public static final String ATTR_ALIAS      = "alias";       // attribute

    public String getProtocol()
    {
        return m_protocol;
    }

    public void setProtocol( String proto )
    {
        m_protocol = proto;
    }

    public String getAddress()
    {
        return m_address;
    }

    public void setAddress( String addr )
    {
        m_address = addr;
    }

    public String getAlias()
    {
        return m_alias;
    }

    public void setAlias( String al )
    {
        m_alias = al;
    }


    private String  m_protocol;
    private String  m_address;
    private String  m_alias;
}
