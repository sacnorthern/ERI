/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.xml;

import java.util.Arrays;
import java.util.List;


/**
 *
 * @author brian
 */
public class OutputGroupBean
    extends AbstractInputOutputGroup
{
    public static final String PROP_ELEMENT_NAME = "outputGroup";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        return Arrays.asList( ATTR_FIRST, ATTR_LAST );
    }

    public OutputGroupBean()
    {
        m_is_output = true;
    }

}
