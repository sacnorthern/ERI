/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.util.List;


/**
 *  Interface to XML "bean" elements that have <em>no</em> change-events generated.
 *  Properties are not "bound" and so there are no change events when set.
 *  Sub-class objects are for data storage and interchange, not really for use directly
 *  in an application program.
 *
 * @author brian
 */
public interface XmlEntityBean
{

    /**
     *  String to use in {@link #toString()} when a null-reference is encountered.
     */
    public final   String  NULL_OBJECT_REF_STRING = "null";

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
