/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.util.List;

/**
 *
 * @author brian
 */
public interface XmlEntityBean
{
    /*** Return name of this element, e.g, "pinSetList" */
    public String getElementName();

    /***
     *  Return list of attributes for this element, or null if none supported.
     *  The returned List<> can be one whose size cannot change, i.e. made by
     *  {@code Arrays.asList()}.
     *
     * @return List<String> of attributes, or null if attrs not allowed on element.
     */
    public List<String> getAttributeList();
}
