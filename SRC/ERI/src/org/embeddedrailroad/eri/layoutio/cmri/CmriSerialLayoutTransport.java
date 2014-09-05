/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import java.util.logging.Logger;

import gnu.io.*;        // use RXTX, version 2.2+
import java.util.*;
import java.io.*;

import com.crunchynoodles.util.StringUtils;
import java.util.logging.Level;
import org.embeddedrailroad.eri.layoutio.AbstractLayoutIoTransport;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.LayoutIoTransport;

/**
 *
 * @author brian
 */
public class CmriSerialLayoutTransport extends AbstractLayoutIoTransport
{

    /* package */ CmriSerialLayoutTransport( LayoutIoProvider owner )
    {
        super(owner);
    }

    @Override
    public String getName() { return m_owner.getName(); }

    //------------------------  POLLING MANAGEMENT  ---------------------------

    @Override
    public void setPolling( boolean runPolling )
    {
        this.m_is_polling = runPolling;
    }

    @Override
    public boolean isPolling()
    {
        return this.m_is_polling;
    }

    @Override
    public boolean attach()
    {
        String  wantedPortName = (String) this.getProperty( PROP_PORT );
        String  settings_all = (String) this.getProperty( PROP_SETTINGS );

        if( StringUtils.emptyOrNull( wantedPortName ) || StringUtils.emptyOrNull( settings_all ) )
        {
            return false;
        }

        //  Hunt for matching com-port.
        //  see http://www.codeproject.com/Questions/450480/How-communicate-with-serial-port-in-Java
        Enumeration  portIdentifiers  = CommPortIdentifier.getPortIdentifiers();

        //
        // Check each port identifier if
        // (a) it indicates a serial (not a parallel) port, and
        // (b) matches the desired name.
        //
        CommPortIdentifier  portId = null; // will be set if port found

        while ( portIdentifiers.hasMoreElements() )
        {
            CommPortIdentifier  pid = (CommPortIdentifier) portIdentifiers.nextElement();
            if( pid.getPortType() == CommPortIdentifier.PORT_SERIAL &&
                pid.getName().equalsIgnoreCase(wantedPortName) )
            {
                portId = pid;
                break;
            }
        }
        if(portId == null)
        {
            LOG.log( Level.WARNING, "Serial port \"{0}\" not found.", wantedPortName );
            return false;
        }

        //
        // Use port identifier for acquiring the port
        //
        SerialPort  port = null;
        try {
            port = (SerialPort) portId.open(
                                    getName(),  // Name of the application asking for the port
                                    3000        // Wait max. 3 sec. to acquire port
                                    );
        } catch(PortInUseException ex) {
            LOG.log( Level.WARNING, String.format( "Serial port \"{0}\" already in use.", wantedPortName ), ex );
            return false;
        }
        //
        // Now we are granted exclusive access to the particular serial
        // port. We can configure it and obtain input and output streams.
        //
        final String[]  settings = settings_all.split( "[,;]" );

        //
        // Set all the params.
        // This may need to go in a try/catch block which throws UnsupportedCommOperationException
        //
        try {
            port.setSerialPortParams(
                            Integer.parseInt(settings[0]),   // first is baud rate.
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE );
        }
        catch( NumberFormatException | UnsupportedCommOperationException ex )
        {
            LOG.log( Level.WARNING, String.format( "Serial port \"{0}\" can't be set.", wantedPortName ), ex );
            port.close();
            return false;
        }

        m_port = port;

        return true;
    }

    @Override
    public void detach()
    {
        setPolling( false );

        try
        {
            m_lock.writeLock().wait();
            if( m_port != null )
            {
                m_port.close();
                m_port = null;
            }
            m_lock.writeLock().unlock();
        }
        catch( InterruptedException ex )
        {
            // while waiting control-C hit, so lock never obtained.
            //  Rrety the operation unsafely, oh well.
            if( m_port != null )
            {
                m_port.close();
                m_port = null;
            }
        }

    }

    //---------------------------  INSTANCE VARS  -----------------------------

    @Override
    protected String[] _getKnownPropertyKeys()
    {
        return m_key_list;
    }

    public final static String  PROP_TIMEOUT = "timeout";
    public final static String  PROP_PORT    = "port";
    public final static String  PROP_SETTINGS = "settings";
    public final static String  PROP_DISCOVERY_RATE = "discoverRate";

    protected final String[]  m_key_list = new String[] { PROP_TIMEOUT, PROP_PORT, PROP_SETTINGS, PROP_DISCOVERY_RATE };

    //---------------------------  INSTANCE VARS  -----------------------------

    transient protected SerialPort  m_port;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriLayoutModelImpl.class.getName() );
}
