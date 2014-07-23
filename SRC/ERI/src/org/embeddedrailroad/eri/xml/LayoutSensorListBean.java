/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.List;

/**
 *
 * @author brian
 */
public class LayoutSensorListBean
    implements XmlEntityBean
{
    public static final String PROP_ELEMENT_NAME = "layoutSensorList";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return null;
    }

    public LayoutSensorListBean()
    {
    }

    // ----------------------------------------------------------------------------


}
