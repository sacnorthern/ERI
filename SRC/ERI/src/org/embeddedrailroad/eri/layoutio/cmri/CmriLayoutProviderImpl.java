/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio.cmri;

import java.util.HashMap;
import java.util.logging.Logger;
import org.embeddedrailroad.eri.layoutio.IoTransportManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.LayoutIoTransport;

/**
 *  Provides instances of
 * @author brian
 */
public class CmriLayoutProviderImpl implements LayoutIoProvider
{

    public CmriLayoutProviderImpl()
    {
        m_io_model = new CmriLayoutModelImpl();
        m_channels = new HashMap<Integer, LayoutIoTransport>();
    }

    @Override
    public String   getName()
    {
        return m_io_model.getIoSystemName();
    }


    //-------------------------  TRANSPORTS  --------------------------

    @Override
    public HashMap<Integer, LayoutIoTransport> getTransportChannelList()
    {
        // Return a copy!
        return new HashMap<Integer, LayoutIoTransport>( m_channels );
    }

    @Override
    public boolean openChannel( Integer channel )
    {
        //  If already open, then good.
        if( m_channels.containsKey( channel ) )
        {
            m_channels.get( channel ).setPolling( false );
            return true;
        }

        return false;
    }


    //--------------------  Addressing Conversion  --------------------

    @Override
    public Object   convertUnitAddressString(String addr)
            throws IllegalArgumentException, NumberFormatException
    {
        return Integer.decode( addr );
    }

    @Override
    public Object   convertIoBitAddressString(String bit)
            throws IllegalArgumentException, NumberFormatException
    {
        return Integer.decode( bit );
    }

    @Override
    public Object   createAddressBit( String addr, String bit, String mode )
            throws IllegalArgumentException, NumberFormatException
    {
        Integer  ua = (Integer) convertUnitAddressString( addr );
        Integer  ba = (Integer) convertIoBitAddressString( bit );

        return null;
    }

    @Override
    public Object   createAddressBitRange( String addr, String bit_start, String bit_end, String mode )
            throws IllegalArgumentException, NumberFormatException
    {
        return null;
    }

    //------------------------  INSTANCE VARS  ------------------------

    protected CmriLayoutModelImpl   m_io_model;

    protected HashMap<Integer, LayoutIoTransport>   m_channels;

    /***  Logging output spigot. */
    private static final Logger LOG = Logger.getLogger( CmriLayoutProviderImpl.class.getName() );

}
