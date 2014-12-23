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
