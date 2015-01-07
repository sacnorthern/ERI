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
