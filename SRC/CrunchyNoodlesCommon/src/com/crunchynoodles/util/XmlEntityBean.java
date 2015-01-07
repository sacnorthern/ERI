/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
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
