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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import javax.swing.event.EventListenerList;

// import org.ini4j.*;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;


/***
 * -----------------------  FAKE OSGi BUNDLE CONTEXT  ---------------------- <br>
 *
 * BundleContext - A bundle's execution context within the Framework.
 * The context is used to grant access to other methods so that this bundle can interact with the Framework.
 *  <p>
 * BundleContext methods allow a bundle to: <ul>
 *  <li> Subscribe to events published by the Framework.
 *  <li> Register service objects with the Framework service registry.
 *  <li> Retrieve ServiceReferences from the Framework service registry.
 *  <li> Get and release service objects for a referenced service.
 *  <li> Install new bundles in the Framework.
 *  <li> Get the list of bundles installed in the Framework.
 *  <li> Get the Bundle object for a bundle.
 *  <li> Create {@link File} objects for files in a persistent storage area provided for the bundle by the Framework.
 * </ul>
 * "A {@code BundleContext} object will be created for a bundle when the bundle
 *  is started. The {@code Bundle} object associated with a {@code BundleContext}
 *  object is called the <em>context bundle</em>."
 * <p>
 *  @see also "osgi.core-6.0.0.pdf"
 * @author brian
 */

public class FakeOSGiBundleContext implements BundleContext
{
    final public  Framework   m_reference;
    final private Bundle     m_bundle;

    /*** Bundles that are interested in our state changes. */
    final private transient EventListenerList  m_bundleListener;


    public FakeOSGiBundleContext( Framework obj )
    {
        this.m_bundleListener = new EventListenerList ();
        this.m_bundle = null;
        this.m_reference = obj;
    }

    @Override
    public String getProperty( String string )
    {
        // this.getBundle().
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    // ---------------------------  INSTALL BUNDLE  --------------------------

    @Override
    public Bundle getBundle()
    {
        return m_bundle;
    }

    @Override
    public Bundle installBundle( String location, InputStream input )
            throws BundleException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bundle installBundle( String location )
            throws BundleException
    {
        return installBundle( location, null );
    }

    @Override
    public Bundle getBundle( long l )
    {
        return m_bundle;
    }

    @Override
    public Bundle[] getBundles()
    {
        // "4.5.1 : getBundles() - Returns an array of the bundles currently installed in the Framework."
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    // --------------------------  LISTENERS AND FIRES  ----------------------

    @Override
    public void addServiceListener( ServiceListener sl, String filter )
            throws InvalidSyntaxException
    {
        // "Adds the specified ServiceListener object with the specified filter to
        //  the context bundle's list of listeners.  See {@link Filter} for a description
        //  of the filter syntax. ServiceListener objects are notified when a
        //  service has a lifecycle state change."

        m_bundleListener.add( ServiceListener.class, sl);
    }

    @Override
    public void addServiceListener( ServiceListener sl )
    {
        m_bundleListener.add( ServiceListener.class, sl);
    }

    @Override
    public void removeServiceListener( ServiceListener sl )
    {
        m_bundleListener.remove(ServiceListener.class, sl);
    }

    @Override
    public void addBundleListener( BundleListener bl )
    {
        // "Adds the specified BundleListener object to the context bundle's list of
        //  listeners if not already present. BundleListener objects are notified when
        //  a bundle has a lifecycle state change."
        m_bundleListener.add( BundleListener.class, bl);
    }

    @Override
    public void removeBundleListener( BundleListener bl )
    {
        m_bundleListener.remove(BundleListener.class, bl);
    }

    /***
     *  Send bundle event to bundle listeners.
     * <p>
     *  For {@link bundleEvent}, the 'origin' field is the bundle that is the origin of the event.
     *  For the event type INSTALLED, this is the bundle whose context was used to install the bundle.
     *  Otherwise it is the bundle itself, i.e. bundleEvent.origin == bundleEvent.bundle .
     *
     * @param bundleEvent this, that, and why.
     */
    protected void fireBundleEvent( BundleEvent bundleEvent )
    {
        BundleListener[] listeners = m_bundleListener.getListeners( BundleListener.class );

        for( BundleListener bl : listeners )
        {
            // pass the event to the listener's event dispatch method
            bl.bundleChanged(bundleEvent);
        }

    }

    @Override
    public void addFrameworkListener( FrameworkListener fl )
    {
        m_bundleListener.add( FrameworkListener.class, fl);
    }

    @Override
    public void removeFrameworkListener( FrameworkListener fl )
    {
        m_bundleListener.remove( FrameworkListener.class, fl);
    }

    // ----------------------  SERVICE REFERENCES  ---------------------------

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
            throws InvalidSyntaxException
    {
        // "Creates a Filter object. This Filter object may be used to match a
        //  ServiceReference object or a Dictionary object."
        return FrameworkUtil.createFilter(string);
    }

    @Override
    public Bundle getBundle( String string ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    // -----------------------------  OBSOLETE METHODS  ----------------------

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
}


