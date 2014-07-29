/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.util.List;
import java.util.Arrays;

/**
 *
 * @author brian
 */
public interface XmlEntityBean
{
    /***
     *  Return name of this element, e.g, "pinSetList"
     *  @return string of this XML element.
     */
    public String getElementName();

    /***
     *  Return list of attributes for this element, or null if none supported.
     *  The returned {@link List} can be one whose size cannot change, i.e. made by
     *  {@link java.util.Arrays#asList(java.lang.Object...) }.
     *
     * @return {@link List} of attributes, or null if attrs not allowed on element.
     */
    public List<String> getAttributeList();
}
