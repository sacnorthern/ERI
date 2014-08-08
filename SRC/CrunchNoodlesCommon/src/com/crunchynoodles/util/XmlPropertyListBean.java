/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util;

import java.util.List;

/**
 *  XML property-list that contains zero or more property-items.
 *  It has no attributes.
 *
 * <p>
 * {@code <!ELEMENT propertyList (property*)>}
 *
 * @see XmlPropertyBean
 * @author brian
 */
public class XmlPropertyListBean
        extends AbstractXmlEntityWithPropertiesBean
{

    public XmlPropertyListBean()
    {
    }

    @Override
    public String getElementName() {
        return "propertyList";
    }

    @Override
    public List<String> getAttributeList() {
        return null;
    }
}
