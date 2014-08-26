/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.xml;

import java.util.Arrays;
import java.util.List;
import com.crunchynoodles.util.AbstractXmlEntityWithPropertiesBean;
import java.util.logging.Logger;

/**
 *  XML element <b>network</b> that holds a <b>propertyList</b> about how to communicate.
 *  A network is used to move protocol messages between the host-computer and nodes.
 *  Uses {@link AbstractXmlEntityWithPropertiesBean} to hold properties.<p>
 *
 *  {@code <!ELEMENT network (propertyList)>} <br>
 *  {@code <!ATTLIST network enabled  %Boolean;  "yes">}
 * @author brian
 */
public class NetworkBean
    extends AbstractXmlEntityWithPropertiesBean
{
    public static final String PROP_ELEMENT_NAME = "network";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_ENABLED );
    }

    public NetworkBean()
    {
        super();
    }

    // ----------------------------------------------------------------------------

    public static final String  ATTR_ENABLED = "enabled";
    protected boolean    m_enabled;

    /**
     *  Is communication with this bank enabled?
     * @return true of communication is enabled.
     */
    public boolean   isEnabled()
    {
        return m_enabled;
    }

    public void     setEnabled( boolean enabled )
    {
        m_enabled = enabled;
        logger.info( "XML comm " + ATTR_ENABLED + " = " + (enabled ? "YES" : "NO") );
    }

    private static final Logger     logger = Logger.getLogger( NetworkBean.class.getName() );

}