/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.osgi;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

// import org.ini4j.*;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * -----------------------  FAKE OSGi BUNDLE CONTEXT  ----------------------
 *
 * @author brian
 */


public class FakeOSGiBundleContext implements BundleContext
{
    final public  Object  m_reference;
    final private List<Bundle>  m_bundle;

    public FakeOSGiBundleContext( Object obj )
    {
        m_reference = obj;
        m_bundle = new ArrayList<Bundle>(1);
    }

    @Override
    public String getProperty( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle getBundle()
    {
        return m_bundle.get( 0 );
    }

    @Override
    public Bundle installBundle( String string, InputStream in )
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle installBundle( String string )
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle getBundle( long l ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle[] getBundles() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addServiceListener( ServiceListener sl, String string )
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addServiceListener( ServiceListener sl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeServiceListener( ServiceListener sl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addBundleListener( BundleListener bl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeBundleListener( BundleListener bl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addFrameworkListener( FrameworkListener fl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFrameworkListener( FrameworkListener fl ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceRegistration<?> registerService( String[] strings, Object o, Dictionary<String, ?> dctnr ) {
        // NOTE: Class Dictionary<> is obsolete. New implementations should implement the Map interface, rather than extending this class.
        throw new UnsupportedOperationException( "Obsolete, not supported." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceRegistration<?> registerService( String string, Object o, Dictionary<String, ?> dctnr ) {
        // NOTE: Class Dictionary<> is obsolete. New implementations should implement the Map interface, rather than extending this class.
        throw new UnsupportedOperationException( "Obsolete, not supported." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> ServiceRegistration<S> registerService( Class<S> type, S s, Dictionary<String, ?> dctnr ) {
        // NOTE: Class Dictionary<> is obsolete. New implementations should implement the Map interface, rather than extending this class.
        throw new UnsupportedOperationException( "Obsolete, not supported." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> ServiceRegistration<S> registerService( Class<S> type, ServiceFactory<S> sf, Dictionary<String, ?> dctnr ) {
        // NOTE: Class Dictionary<> is obsolete. New implementations should implement the Map interface, rather than extending this class.
        throw new UnsupportedOperationException( "Obsolete, not supported." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceReference<?>[] getServiceReferences( String string, String string1 )
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceReference<?>[] getAllServiceReferences( String string, String string1 )
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceReference<?> getServiceReference( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> ServiceReference<S> getServiceReference( Class<S> type ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences( Class<S> type, String string )
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> S getService( ServiceReference<S> sr ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ungetService( ServiceReference<?> sr ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects( ServiceReference<S> sr ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getDataFile( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Filter createFilter( String string )
            throws InvalidSyntaxException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle getBundle( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

}


