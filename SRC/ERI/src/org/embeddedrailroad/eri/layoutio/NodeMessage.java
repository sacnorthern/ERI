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

import org.embeddedrailroad.eri.ctc.exceptions.PacketCheckException;
import org.embeddedrailroad.eri.ctc.exceptions.PacketUnknownException;

/***
 *   Interface of messages that can be sent and received by nodes, using some protocol.
 *
 * @author brian
 * @param <TUnitAddr> class-type for addressing a node, e.g Integer or IpAddress.
 */
public interface NodeMessage< TUnitAddr extends Comparable<TUnitAddr> >
{

    /***
     *  Return reference to provider of this protocol.
     * @return LayoutIoProtocolProvider instance
     */
    public LayoutIoProtocolProvider   getProtocolProvider();

    /***
     *  Set up this object with a packet from a node, regardless of operation-code and any check-value.
     *  This method takes the place of an instance-constructor that would accept a packet
     *  from a node, or a factory method that returns a specific instance to encapsulate
     *  message's type.
     *<p>
     *  Packets are accept if mal-formed or the operation code is unknown.
     *  This allows for diagnostics to get a packet in a pretty-printed string.
     *
     * @param fromNode bytes received.
     */
    public void         acceptRxPacket( byte[] fromNode );

    /***
     *  Perform validation on received packet.
     *  If good, then does not throw an exception.
     */
    public void         validateMessage()
            throws PacketCheckException, PacketUnknownException;

    /*---------------------------  Properties  ------------------------------*/

    /***
     *  Set all bytes of the message, include address.
     *  And data-link or escaping will be added during transmission or stripped during reception.
     * @param allBytes bytes of message
     */
    public void         setAllBytes( byte[] allBytes );

    /***
     *  Set all bytes of the message, but not the address byte(s).
     *  If the address straddles bits of one or more bytes, then those bits should be zero.
     *  They will be filled in during transmission.
     *  And data-link or escaping will be added during transmission or stripped during reception.
     * @param allBytes bytes of message, skipping address bytes if the addres uses the whole byte.
     */
    public void         setBytesExceptAddress( byte[] bytesSansAddr );

    /***
     *  Return message bytes that a human would like to know about, which excludes data-link
     *  framing and any escaping that must occur.
     *
     *  @return message bytes
     */
    public byte[]       getMessageBytes();

    /***
     *  Return all bytes that must be sent, including any header and/or trailer, byte-stuffing,
     *  escaping, etc.
     *
     * @return all bytes that must be sent for this message to be xmitted to a node.
     */
    public byte[]       getDataLinkBytes();

    /***
     *  Sets the address of this message by remembering it outside of any message bytes.
     *  When {@link #getMessageBytes() } or {{@link #getDataLinkBytes() } is called, the
     *  address is inserted into the result.
     *
     * @param addr unit address.
     */
    public void         setAddress( TUnitAddr addr );

    /***
     *  Returns the unit address if set, otherwise deciphers address in message-bytes.
     * @return address if known, else {@code null} if unknown.
     */
    public TUnitAddr    getAddress();
}
