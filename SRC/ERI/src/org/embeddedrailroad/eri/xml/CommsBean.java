/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.xml;

import java.util.Arrays;
import java.util.List;
import com.crunchynoodles.util.AbstractXmlEntityWithPropertiesBean;

/**
 *  XML element <b>comms</b> that holds a <b>propertyList</b> about how to communicate.
 *  Uses {@link AbstractXmlEntityWithPropertiesBean} to hold properties.<p>
 *
 *  {@code <!ELEMENT comms (propertyList)>} <br>
 *  {@code <!ATTLIST comms enabled  %Boolean;  "yes">}
 * @author brian
 */
public class CommsBean
    extends AbstractXmlEntityWithPropertiesBean
{
    public static final String PROP_ELEMENT_NAME = "comms";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_ENABLED );
    }

    public CommsBean()
    {
        super();
    }

    // ----------------------------------------------------------------------------

    public static final String  ATTR_ENABLED = "enabled";
    protected boolean    m_enabled;

    public boolean   getEnabled()
    {
        return m_enabled;
    }

    public void     setEnabled( boolean enabled )
    {
        m_enabled = enabled;
    }

}
