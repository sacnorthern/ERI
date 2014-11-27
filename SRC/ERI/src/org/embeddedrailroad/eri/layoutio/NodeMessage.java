/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
     * @return LayoutIoProvider instance
     */
    public LayoutIoProvider   getProtocolProvider();

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
     *
     * @return true if passes smell-test, false otherwise.
     */
    public void         validateMessage()
            throws PacketCheckException, PacketUnknownException;

    /*---------------------------  Properties  ------------------------------*/

    public void         setAllBytes( byte[] allBytes );
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
    public byte[]       getAllBytes();

    public void         setAddress( TUnitAddr addr );

    public TUnitAddr    getAddress();
}
