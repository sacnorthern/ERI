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

package org.embeddedrailroad.eri.layoutio.cmri;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.LayoutIoTransport;

/**
 *  Provides instances of protocol transports; this object is effectively a singleton.
 *
 * @author brian
 */
public class CmriLayoutProviderImpl implements LayoutIoProvider
{

    private CmriLayoutProviderImpl()
    {
        m_io_model = new CmriLayoutModelImpl();
        m_channels = new HashMap<Integer, CmriSerialLayoutTransport>();
    }

    /***
     * static method to get instance
     * @return singleton instance of manager
     * @see http://stackoverflow.com/questions/18093735/double-checked-locking-in-singleton
     */
    public static CmriLayoutProviderImpl getInstance()
    {
        if (s_instance == null) { // first time lock
            synchronized (CmriLayoutProviderImpl.class) {
                if (s_instance == null) {  // second time lock
                    s_instance = new CmriLayoutProviderImpl();
                }
            }
        }
        return s_instance;
    }

    @Override
    public String   getName()
    {
        return "C/MRI" ;
    }

    @Override
    public String   getVersionString()
    {
        return "0.0.1 build 1" ;
    }

    @Override
    public String getSystemManufacturer() {
        return "JLC Enterprises" ;
    }

    @Override
    public String   getLongDescription()
    {
        return "The CMRI protocol by Dr. Bruce Chubb." ;
    }

    //-----------------------------  TRANSPORTS  ------------------------------

    @Override
    public HashMap<Integer, LayoutIoTransport> getTransportChannelList()
    {
        // Return a copy!
        return new HashMap<Integer, LayoutIoTransport>( m_channels );
    }

    @Override
    public LayoutIoTransport makeChannel( String physical, Integer channel )
    {
        LOG.log( Level.INFO, "CmriLayoutProviderImpl#makeChannel() as {0}", physical );

        //  This is interesting way to manage sub-class creation without the
        //  sub-class knowing it will be created:
        //  http://stackoverflow.com/questions/3001490/creating-an-instance-of-a-subclass-extending-an-abstract-class-java

        //  If don't already got that channel running, make it first.
        CmriSerialLayoutTransport  transport = m_channels.get( channel );
        if( transport == null )
        {
            transport = new CmriSerialLayoutTransport( this );

            m_channels.put( channel, transport );
        }

        return( transport );
    }


    //------------------------  Addressing Conversion  ------------------------

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

    private static CmriLayoutProviderImpl       s_instance;

    //----------------------------  INSTANCE VARS  ----------------------------

    protected CmriLayoutModelImpl   m_io_model;

    protected HashMap<Integer, CmriSerialLayoutTransport>   m_channels;

    /***  Logging output spigot. */
    private transient static final Logger LOG = Logger.getLogger( CmriLayoutProviderImpl.class.getName() );

}
