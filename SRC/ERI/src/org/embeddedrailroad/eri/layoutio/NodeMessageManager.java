/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

/**
 *  Interface for protocol-specific message creation and formatting, which is generally a singleton.
 *
 * @author brian
 */
public interface NodeMessageManager
{

    /***
     *  Set up this object with a packet from a node.
     *  This method takes the place of an instance-constructor that would accept a packet
     *  from a node, or a factory method that returns a specific instance to encapsulate
     *  message's type.
     *<p>
     *  Packets are accept if mal-formed or the operation code is unknown.
     *  This allows for diagnostics to get a packet in a pretty-printed string.
     *
     * @param fromNode bytes received.
     * @return NodeMessage instance constructed for this packet
     */
    public NodeMessage  acceptRxPacket( byte[] fromNode );

    /***
     *
     * @param mesg
     * @param options formatting options.
     * @return String equivalent of message.
     */
    public String   prettyFormat( NodeMessage mesg, int options );

    public final static int     PKT_FMT_LINEAR          = 1 << 1;
    public final static int     PKT_FMT_16_BYTE_DUMP    = 1 << 2;
    public final static int     PKT_FMT_8_BYTE_DUMP     = 1 << 3;
    public final static int     PKT_FMT_HTML_TABLE_ROW  = 1 << 4;
    public final static int     PKT_FMT_HTML_5          = 1 << 5;   // If not HTML5, then 3.2

}
