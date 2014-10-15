/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import java.util.logging.Logger;

import gnu.io.*;        // use RXTX, version 2.2+
import java.util.*;
import java.util.logging.Level;

import com.crunchynoodles.util.StringUtils;
import com.crunchynoodles.util.XmlPropertyBean;
import org.embeddedrailroad.eri.layoutio.AbstractLayoutIoTransport;
import org.embeddedrailroad.eri.layoutio.LayoutIoController;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;


/***
 *   Create connection to an IO port and make the {@link LayoutIoController} object with our
 *   own C/MRI model (database).
 * <p>
 *  Java RXTX library, see: http://users.frii.com/jarvi/rxtx/download.html
 * <p>
 *   To install the libraries (instructions from <a href="http://www.jcontrol.org/download/readme_rxtx_en.html">JControl</a>):
 * <ol>
 *     <li> Copy rxtxSerial.dll to %JAVA_HOME%\bin, (%JAVA_HOME% is the folder where JRE is installed on your system; e.g. c:\Program Files\Java\j2re1.4.1_01)
 *     <li> Copy RXTXcomm.jar to %JAVA_HOME%\lib\ext
 * </ol>
 *
 * @author brian
 */
public class CmriSerialLayoutTransport extends AbstractLayoutIoTransport
{

    /* package */ CmriSerialLayoutTransport( LayoutIoProvider owner )
    {
        super(owner);
        m_model = new CmriLayoutModelImpl();
    }

    @Override
    public String getName() { return m_owner.getName(); }

    //------------------------  POLLING MANAGEMENT  ---------------------------

    @Override
    public void setPolling( boolean runPolling )
    {
        if( this.m_poller != null )
            this.m_poller.setPolling( runPolling );
    }

    @Override
    public boolean isPolling()
    {
        if( this.m_poller != null )
            return this.m_poller.isPolling();

        return false;
    }

    /***
     *  Open up the port in PROP_PORT using PROP_SETTINGS values.
     *
     * @return true on success, false when failed.
     *
     * @throws java.lang.ClassCastException if wrong type in property bean.
     * @throws java.lang.UnsatisfiedLinkError if OS-specific "rxtxSerial.dll" or "rxtxSerial.so" not found.
     * @throws IllegalArgumentException if discoveryRate less-than or equal to 0, or above 1.0
     */
    @Override
    public synchronized boolean attach()
    {
        XmlPropertyBean  wantedPortBean = this.getProperty( PROP_PORT );
        String  wantedPortName = (String) wantedPortBean.getValue();

        XmlPropertyBean  settings_all_bean = this.getProperty( PROP_SETTINGS );
        String  settings_all = (String) settings_all_bean.getValue();

        if( StringUtils.emptyOrNull( wantedPortName ) || StringUtils.emptyOrNull( settings_all ) )
        {
            return false;
        }

        //  Hunt for matching com-port.
        //  see http://stackoverflow.com/questions/6924516/open-and-close-serial-ports
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
                                    getName(),  // Use name of the application asking for the port.
                                    3000        // Wait max. 3 sec. to acquire port.
                                    );
        } catch(PortInUseException ex) {
            LOG.log( Level.WARNING, String.format( "Serial port \"%1$s\" already in use.", wantedPortName ), ex );
            return false;
        }
        //
        // Now we are granted exclusive access to the particular serial port.
        // We can configure it and obtain input and output streams.
        //
        final String[]  settings = settings_all.split( "[,;]" );
        int     baud_rate = 1000;

        //
        // Set all the params.
        // This may need to go in a try/catch block which throws UnsupportedCommOperationException
        //
        try {
            baud_rate = Integer.parseInt( settings[0] );
            port.setSerialPortParams(
                            baud_rate,                      // first is baud rate.
                            SerialPort.DATABITS_8,          // always 8 bit.
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE );       // always NO parity.
        }
        catch( NumberFormatException | UnsupportedCommOperationException ex )
        {
            LOG.log( Level.WARNING, String.format( "Serial port \"%1$s\" can't be set.", wantedPortName ), ex );
            port.close();
            return false;
        }

        //  Port now open. :)  Only reason to keep it around is to close on shutdown.
        m_port = port;

        this.m_poller = new CmriPollMachine( m_port, baud_rate, m_model );

        XmlPropertyBean  rateBean = this.getProperty( PROP_DISCOVERY_RATE );
        float   rate = DEFAULT_DISCOVERY_RATE;

        if( rateBean != null )
        {
            try
            {
                rate = (float) rateBean.getValue();
            }
            catch( Exception ex )
            {
                LOG.log( Level.WARNING, "Unable to set discovery rate." );
            }

            this.m_poller.setRecoveryRate( rate );
        }

        return true;
    }

    @Override
    public synchronized void detach()
    {
        setPolling( false );

        if( m_port != null )
        {
            if( m_poller != null )
            {
                m_poller.shutdown();
                m_poller = null;
            }

            m_port.notifyOnDataAvailable(false);
            m_port.removeEventListener();

            m_port.close();
            m_port = null;
        }

    }

    //------------------------  NON-PUBLIC METHODS  ---------------------------

    @Override
    protected String[] _getKnownPropertyKeys()
    {
        return m_key_list;
    }

    //---------------------------  INSTANCE VARS  -----------------------------

    /*** How fast new units are brought on-line, per poll cycle.  E.g. 0.5 means every two cycles. */
    public final static float   DEFAULT_DISCOVERY_RATE = 0.5f;

    public final static String  PROP_TIMEOUT = "timeout";           // in milliseconds
    public final static String  PROP_PORT    = "port";              // string name
    public final static String  PROP_SETTINGS = "settings";         // 9600,8,n,1
    public final static String  PROP_DISCOVERY_RATE = "discoverRate";   // float, per poll cycles.

    protected final String[]  m_key_list = new String[] { PROP_TIMEOUT, PROP_PORT, PROP_SETTINGS, PROP_DISCOVERY_RATE };

    //---------------------------  INSTANCE VARS  -----------------------------

    transient protected SerialPort  m_port;

    transient protected CmriPollMachine  m_poller;

    protected final CmriLayoutModelImpl   m_model;


    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriLayoutModelImpl.class.getName() );
}
