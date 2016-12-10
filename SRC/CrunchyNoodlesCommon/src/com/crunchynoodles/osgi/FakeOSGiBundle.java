/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
 *
 * @author brian
 */
public class FakeOSGiBundle implements Bundle
{

    /***
     *  The Framework creates a new bundle.  A unique identifier is assigned.
     *  "Bundles have a natural ordering such that if two {@code Bundle}s have the
     *  same {@link #getBundleId() bundle id} they are equal."
     *
     * @param ident Framework's bundle ID.
     * @param url_wherefrom "file:YOUR.jar" (no forward-slashes with "file:").
     */
    public FakeOSGiBundle( long ident, String url_wherefrom )
    {
        m_state = UNINSTALLED;
        m_ident = ident;
        m_last_update_time_millis = 0;
        m_wherefrom_url = url_wherefrom;
    }

    // -----------------------  Settors and Gettors  --------------------------

    @Override
    public int getState()
    {
        // "Bundle states are expressed as a bit-mask though a bundle can only be in one state at any time."
        return m_state;
    }

    @Override
    public long getBundleId()
    {
        return m_ident;
    }

    @Override
    public boolean hasPermission(Object permission)
    {
        // @throws IllegalStateException If this bundle has been uninstalled.
        if( UNINSTALLED == m_state )
        {
            throw new IllegalStateException( "bundle is uninstalled." );
        }
        return true;
    }

    // ---------------------------  Operations  -------------------------------

    @Override
    public void start() throws BundleException
    {
        start(0);
    }

    @Override
    public void start(int options) throws BundleException
    {
        if( UNINSTALLED == m_state )
        {
            // If this bundle's state is 'UNINSTALLED' then an IllegalStateException is thrown.
            throw new IllegalStateException( "bundle's state is 'UNINSTALLED'" );
        }

        // .. get bundle started, depending on settings ..

        // If this bundle's state is 'STARTING' then this method returns immediately.
        if( STARTING == m_state )
        {
            return ;
        }
        _setNewState( STARTING );

        // .. more steps ..
    }


    @Override
    public void stop() throws BundleException
    {
        stop(0);
    }

    @Override
    public void stop( int state ) throws BundleException
    {
        if( UNINSTALLED == getState() )
        {
            // if this bundle's state is 'UNINSTALLED' then an IllegalStateException is thrown.
            throw new IllegalStateException( "this bundle's state is 'UNINSTALLED'" );
        }


    }

    // --------------------  Load the Bundle and Class  -----------------------

    @Override
    public void update( InputStream in )
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update()
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void uninstall()
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLocation() {
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
    public URL getResource( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dictionary<String, String> getHeaders( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSymbolicName() {
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
    public BundleContext getBundleContext() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
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
    public <A> A adapt( Class<A> type ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getDataFile( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo( Bundle o ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    //  ---------------------  Implementation Assistance  ---------------------

    protected void _setNewState( int newState )
    {
        // "Bundle states are expressed as a bit-mask though a bundle can only be in one state at any time."
        m_state = newState;
        m_last_update_time_millis = new Date().getTime();
    }

    // --------------------------  Instance Vars  -----------------------------

    protected int   m_state;

    protected long  m_ident;

    /** Time in milliseconds when m_state was last changed. */
    protected long  m_last_update_time_millis;

    protected String  m_wherefrom_url;

}
