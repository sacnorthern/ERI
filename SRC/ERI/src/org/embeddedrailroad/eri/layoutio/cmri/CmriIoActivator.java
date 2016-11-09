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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.framework.BundleContext;

import org.embeddedrailroad.eri.layoutio.LayoutIoProviderManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoActivator;


/**
 *  Starts up or stops a layout communications CMRI provider.
 *  There will be only one instance of each protocol-activator created (though not enforced).
 *  Start up code registers with {@link LayoutIoProviderManager}, and stopping removes
 *  the registration.
 *
 * <p> "Thread safety is particularly important in OSGi since it is intrinsically multithreaded,
 *      but frankly we should always write our code to be thread-safe. " <br>
 *      --- from http://www.eclipsezone.com/eclipse/forums/t97690.html
 *
 *  <p> Modeled after OSGi framework:
 * <br>
 *    &nbsp;  http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleActivator.html
 *
 * @author brian
 */
public class CmriIoActivator implements LayoutIoActivator
{
    /***
     *  Short-and-sweet name used to name ourselves to the {@link LayoutIoProviderManager}.
     *  Contains no chars that might not be legal file name, e.g. no slash, quote, colon, or
     *  control chars.
     */
    public final static String  PLAIN_NAME_STRING = "cmri";

    /***
     *  Public constructor so loader can call newInstance() on class-ref.
     */
    public CmriIoActivator()
    {
        // do nothing.
    }

    /**
     *  We are now part of the system.  For CMRI, this means registration.
     *
     * @param context An opaque object from the {@link LayoutIoProviderManager}.  The {@link BundleContext} is our connection to the dynamic loading system.
     */
    @Override
    public void start( BundleContext context )
    {
        LOG.log( Level.INFO,   "in CmriIoActivator#start(BC)" );
        LOG.log( Level.CONFIG, ".. our class loader is {0}", this.getClass().getClassLoader().getClass().getName() );

        m_bc = context;

        LayoutIoProviderManager  mgr = LayoutIoProviderManager.getInstance();
        m_the_provider = CmriLayoutProviderImpl.getInstance();      // Provider is a singleton.
        mgr.addProvider( PLAIN_NAME_STRING, m_the_provider.getLongDescription(), m_the_provider );
    }

    /**
     *  No longer part of the system, so unregister ourselves.
     *
     * @param context same as passed to {@link #start(org.osgi.framework.BundleContext) }
     */
    @Override
    public void stop( BundleContext context )
    {
        LOG.log( Level.INFO, "in CmriIoActivator#stop(BC)" );

        LayoutIoProviderManager  mgr = LayoutIoProviderManager.getInstance();
        mgr.removeProviderTransport( PLAIN_NAME_STRING );

        // TODO: Close down all transports.

        m_the_provider = null;
        m_bc = null;
    }

    @Override
    public String versionValue()
    {
        return m_the_provider.getVersionString();
    }

    //-----------------------------  INSTANCE VARS  ---------------------------

    /****
     *  "The BundleContext object is only valid during the execution of its context bundle;
     *   that is, during the period from when the context bundle is in the STARTING, STOPPING,
     *   and ACTIVE bundle states."
     *
     *  "The Framework is the only entity that can create BundleContext objects and they are
     *   only valid within the Framework that created them."
     */
    transient private BundleContext       m_bc;

    /***
     *  Provider is a singleton.
     * TODO: Consider removing reference to singleton since can be obtained any time. (BWitt, Nov 2014)
     */
    transient private CmriLayoutProviderImpl   m_the_provider;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriIoActivator.class.getName() );

}
