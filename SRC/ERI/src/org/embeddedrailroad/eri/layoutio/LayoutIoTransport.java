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

package org.embeddedrailroad.eri.layoutio;

import com.crunchynoodles.util.XmlPropertyBean;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author brian
 */
public interface LayoutIoTransport
{
    /*** @return same name as Layout IO Provider. */
    public String getProtocolName();

    /***
     *  Given channel setup properties, attempt to connect and use transport channel.
     *  For serial ports, this method will set the baud rate, parity, stop-bits, etc.
     *  If successful, caller needs to invoke {@code setPolling(true)} to start running.
     *
     * @return {@code true} if attachment successful, {@code false} otherwise.
     */
    public boolean attach();

    /***
     *  Stop polling and disconnect the channel.  Afterwards, other clients can
     *  use said channel.  For serial ports, the COM port is closed.
     *  For TCP or UDP, the server socket is closed and freed.
     */
    public void detach();

    /***
     *  Control polling of units via this single transport channel.
     * @param runPolling true to start polling, false to stop.
     */
    public void setPolling( boolean runPolling );

    /*** @return {@code true} if polling is active, {@code false} otherwise. */
    public boolean isPolling();


    public void setProperty( String prop_name, Object value );

    /***
     *  Set a bunch of properties, not atomic but in a batch.
     *
     * @param propList List of property (beans) to use.
     */
    public void setProperties( List< XmlPropertyBean >  propList );

    /***
     *  Return a property as an XML bean, or null if property is unset.
     * @param prop_name String name of property, which must match letter-case.
     * @return bean if found, or {@code null} if no match.
     */
    public XmlPropertyBean getProperty( String prop_name );

    /***
     *  Return all properties of transport and their values (with type-spec).
     *  Some properties may be default values not set by application.
     *
     * @return  List of XML properties
     */
    public List< XmlPropertyBean >   getAllProperties();
}
