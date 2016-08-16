/***  This file is dedicated to the public domain, 2016 Brian Witt in USA.  ***/

package com.crunchynoodles.util;

import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.impl.xs.opti.AttrImpl;

/**
 *
 * @author brian
 */
public class TestWithAttr extends AttrImpl
{

    public TestWithAttr(Element element, String prefix, String localpart, String rawname, String uri, String value)
    {
        super( element, prefix, localpart, rawname, uri, value );
    }
}
