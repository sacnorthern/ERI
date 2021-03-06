/***  Java-ERI    Java-based Embedded Railroad Interfacing.
 ***  Copyright (C) 2014,  in USA by Brian Witt , bwitt@value.net
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/***
 *  A "bank" is one communication protocol talking with a series of "unit" things over a physical connection.
 *  There may be several banks with the same protocol.
 *  The physical connection can be shared, e.g. "tcp" or "udp", or private and not shared, e.g. "serial".
 *  All units on bank must have a unique address, though any may receive broadcast messages. <br>
 *
 *  <pre> &lt;!ELEMENT bank (comms,unitList)&gt;
 * &lt;!ATTLIST bank
 *            protocol   CDATA  #REQUIRED
 *            address    CDATA  #REQUIRED
 *            physical   (serial|tcp|udp) "serial"
 *            alias      CDATA  ""  &gt;</pre>
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
        return Arrays.asList( ATTR_PROTOCOL, ATTR_ADDRESS, ATTR_PHYSICAL, ATTR_ALIAS );
    }

    public BankBean()
    {
        this.m_unit_list = new ArrayList<UnitBean>();
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
        if( m_unit_list != null )    hc ^= m_unit_list.hashCode();
        if( m_physical != null )        hc ^= m_physical.hashCode();

        return( hc );
    }

    @Override
    public String  toString()
    {
        StringBuilder  sb = new StringBuilder( 100 );

        sb.append( "BankBean:[" );

        sb.append( "m_comms_element=" );
        if( m_comms_element != null )
            sb.append( m_comms_element.toString() );        // NetworkBean
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( ",m_protocol=" );
        if( m_protocol != null )
            sb.append( m_protocol );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( ",m_address=" );
        if( m_address != null )
            sb.append( m_address );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( ",m_physical=" );
        if( m_physical != null )
            sb.append( m_physical );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append(  ",alias=" );
        if( m_alias != null )
        {
            sb.append( '"' ).append( m_alias ).append( '"' );
        }
        else
        {
            sb.append( NULL_OBJECT_REF_STRING );
        }

        sb.append( ",m_unit_list={" );
        if( m_unit_list != null )
            sb.append( m_unit_list.toString() );     // List<UnitBean>
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( "}]" );

        return( sb.toString() );
    }

    // ----------------------------------------------------------------------------

    public static final String ELEMENT_COMMS = "comms";
    protected NetworkBean         m_comms_element;

    public NetworkBean getComms()
    {
        return m_comms_element;
    }

    public void     setNetwork( NetworkBean comms_ele )
    {
        m_comms_element = comms_ele;
    }

    // ----------------------------------------------------------------------------

    public static final String ELEMENT_UNIT_LIST = "unitList";
    protected List<UnitBean>          m_unit_list;

    /***
     *  Return known units in an array, order is arbitrary.
     * @return array with references to units held by BankBean.
     */
    public UnitBean[]  getUnits()
    {
        return (UnitBean[]) m_unit_list.toArray();
    }

    /***
     *  Create a modern Java typed iterator with reference to units held by BankBean.
     * @return Iterator over Unit beans.
     */
    public Iterator<UnitBean> getUnitIterator()
    {
        return m_unit_list.iterator();
    }

    /***
     *  Add another Unit bean to known units.
     *  Caller's object is stored, not copied, not shaken, and not stirred.
     *
     * @param unitElm Another unit.
     */
    public void     addUnit( UnitBean unitElm )
    {
        m_unit_list.add( unitElm );
    }

    /***
     *  Inject the caller's list of {@link UnitBean} objects.
     *  Shallow copy.
     *
     * @param unitList List to shallow copy.
     */
    public void     addUnitList( List<UnitBean> unitList )
    {
        m_unit_list.addAll( unitList );
    }

    // ----------------------------------------------------------------------------

    public static final String ATTR_PROTOCOL   = "protocol";    // attribute

    public String getProtocol()
    {
        return m_protocol;
    }

    public void setProtocol( String proto )
    {
        m_protocol = proto;
    }

    public static final String ATTR_ADDRESS    = "address";     // attribute

    public String getAddress()
    {
        return m_address;
    }

    public void setAddress( String addr )
    {
        m_address = addr;
    }

    public static final String ATTR_ALIAS      = "alias";       // attribute

    public String getAlias()
    {
        return m_alias;
    }

    public void setAlias( String al )
    {
        m_alias = al;
    }

    public static final String ATTR_PHYSICAL   = "physical";    // attribute

    public String getPhysical()
    {
        return m_physical;
    }

    public void setPhysical( String phys )
    {
        m_physical = phys;
    }

    // ----------------------------------------------------------------------------

    private String  m_protocol;
    private String  m_address;
    private String  m_physical;
    private String  m_alias;
}
