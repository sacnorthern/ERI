/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.Arrays;
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
        return Arrays.asList( PROP_PROTOCOL, PROP_ADDRESS, PROP_ALIAS );
    }

    public BankBean()
    {
        m_alias = "";
    }

    // ----------------------------------------------------------------------------

    public static final String PROP_PROTOCOL   = "protocol";    // attribute
    public static final String PROP_ADDRESS    = "address";     // attribute
    public static final String PROP_ALIAS      = "alias";       // attribute

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
