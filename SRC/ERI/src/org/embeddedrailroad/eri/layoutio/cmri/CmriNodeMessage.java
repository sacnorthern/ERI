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
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.layoutio.cmri;

import org.embeddedrailroad.eri.ctc.exceptions.PacketCheckException;
import org.embeddedrailroad.eri.ctc.exceptions.PacketUnknownException;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.NodeMessage;

/**
 *
 * @author brian
 */
public class CmriNodeMessage implements NodeMessage<Integer>
{

    public CmriNodeMessage()
    {
        m_address = 0;
    }

    @Override
    public LayoutIoProvider getProtocolProvider()
    {
        return CmriLayoutProviderImpl.getInstance();
    }

    @Override
    public void acceptRxPacket( byte[] fromNode )
    {
        m_bytes = java.util.Arrays.copyOf( fromNode, fromNode.length );
    }

    @Override
    public void validateMessage()
            throws PacketCheckException, PacketUnknownException
    {
    }

    @Override
    public void setAllBytes( byte[] allBytes ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBytesExceptAddress( byte[] bytesSansAddr ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getMessageBytes() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getAllBytes()
    {
        return java.util.Arrays.copyOf( m_bytes, m_bytes.length );
    }

    @Override
    public void setAddress( Integer addr )
    {
        m_address = addr;
    }

    @Override
    public Integer getAddress()
    {
        return m_address;
    }

    /*---------------------------  Properties  ------------------------------*/

    private Integer         m_address;

    private byte[]          m_bytes;
}
