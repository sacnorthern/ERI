/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2016 in USA by Brian Witt , bwitt@value.net
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

package com.crunchynoodles.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.Constants;


/**
 * "The framework object can be seen as the system bundle, though the framework object and
 *  the system bundle do not have to be identical, implementations are allowed to implement
 *  them in different objects."
 *
 * <p>
 * "Framework instances are created using a {@link FrameworkFactory org.osgi.framework.FrameworkFactory}.
 *  The methods of this interface can be used to manage and control the created framework
 *  instance." <br>
 * "The Framework is the only entity that can
 *  create {@code BundleContext} objects and they are only valid within the
 *  Framework that created them."
 *
 * @author brian
 */
public class FakeOSGiFramework extends FakeOSGiBundle implements Framework
{

    /***
     *  Create new bundle loading framework.
     *  Makes a shallow copy of the map.
     * @param config_settings Provides the sole configuration properties for the framework object.
     */
    public FakeOSGiFramework( Map<String, String> config_settings )
    {
        super( org.osgi.framework.Constants.SYSTEM_BUNDLE_ID );

        m_default_class_loader = ClassLoader.getSystemClassLoader();
        m_latest_id = org.osgi.framework.Constants.SYSTEM_BUNDLE_ID;
        m_map = new HashMap<String, String>( config_settings );
    }

    @Override
    public void init()
            throws BundleException
    {
        m_system_context = new FakeOSGiBundleContext( this );
        System.out.println( m_wherefrom_url );
    }

    @Override
    public void init( FrameworkListener... fls )
            throws BundleException
    {
        init();
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FrameworkEvent waitForStop( long l )
            throws InterruptedException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start()
            throws BundleException
    {
        // "4.6. start - Does nothing because the system bundle is already started."
    }

    @Override
    public void start( int level )
            throws BundleException
    {
        // "4.6. start - Does nothing because the system bundle is already started."
    }

    @Override
    public void stop()
            throws BundleException
    {
        // "4.6. stop - Returns immediately and shuts down the Framework on another thread."
        // TODO: implementate
    }

    @Override
    public void stop( int level )
            throws BundleException
    {
        // "4.6. stop - Returns immediately and shuts down the Framework on another thread."
        // TODO: implementate
    }

    @Override
    public void uninstall()
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update()
            throws BundleException
    {
        // "4.6. update - Returns immediately, then stops and restarts the Framework on another thread."
    }

    @Override
    public void update( InputStream in )
            throws BundleException
    {
        // "4.6. update - Returns immediately, then stops and restarts the Framework on another thread."
    }

    // @Override public long getBundleId()

    @Override
    public String getLocation() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSymbolicName() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> getEntryPaths( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public URL getEntry( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<URL> findEntries( String string, String string1, boolean bln ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <A> A adapt( Class<A> type ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getState()
    {
        return ACTIVE;
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceReference<?>[] getRegisteredServices() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceReference<?>[] getServicesInUse() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasPermission( Object o ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public URL getResource( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary<String, String> getHeaders( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<?> loadClass( String string )
            throws ClassNotFoundException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<URL> getResources( String string )
            throws IOException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BundleContext getBundleContext()
    {
        return m_system_context;
    }

    @Override
    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates( int i ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Version getVersion() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getDataFile( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    // @Override    public int compareTo( Bundle o )

    // -------------------------------  Instance Vars  ----------------------------

    protected transient Map<String, String>   m_map;

    /*** Every bundle gets its own ID, monotonically increasing. */
    protected transient long           m_latest_id;

    protected transient ClassLoader    m_default_class_loader;

    /*** System context , where it all starts. */
    protected transient FakeOSGiBundleContext  m_system_context;
}