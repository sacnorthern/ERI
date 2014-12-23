/***  Java-ERI    Java-based Embedded Railroad Interfacing.
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
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.layoutio.cmri;

// import java.lang.Integer;
import java.util.logging.Logger;
import org.embeddedrailroad.eri.layoutio.AbstractLayoutIoModelIntegerAddress;


/***
 *  Layout Model for one bank of CMRI units, maintained by one CmriLayoutTransport object;
 *  they are paired.
 *  CMRI units are addressed with a single number, from 0 to 31.  One the wire, 65 is added
 *  to the address, e.g. unit #0 address is encoded as 'A', unit #1 address is 'B', etc.
 *
 * <p> See http://www.onjava.com/pub/a/onjava/2004/07/07/genericmvc.html
 *
 * @author brian
 */
public class CmriLayoutModelImpl extends AbstractLayoutIoModelIntegerAddress
{
    public CmriLayoutModelImpl()
    {
    }

    //--------------------------  INSTANCE VARS  -------------------------


    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriLayoutModelImpl.class.getName() );

}
