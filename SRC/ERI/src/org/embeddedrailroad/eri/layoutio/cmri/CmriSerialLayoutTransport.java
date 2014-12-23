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
     *  Port name PROP_PORT is case-insensitive, but must be a "serial port" type.
     *
     * @return true on success, otherwise false when failed and logging message put.
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
            LOG.log( Level.WARNING, "Empty port or settings property, won't open port." );
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
        int     baud_rate;

        if( settings.length <= 1 )
        {
            LOG.log( Level.WARNING, "Need at least a baud-rate in PROP_SETTINGS, but got \"{0}\"", settings_all );
            port.close();
            return false;
        }

        //
        // Set all the params.
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

        //  Port now open. :)  Only reason to keep port reference around is to close on shutdown.
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
                LOG.log( Level.WARNING, "Unable to set discovery rate, using default." );
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
