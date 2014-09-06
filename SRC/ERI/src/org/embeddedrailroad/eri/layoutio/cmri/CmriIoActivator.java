/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
 *  <p> Modeled after OSGi framework:
 * <br>
 *    &nbsp;  http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleActivator.html
 *
 * @author brian
 */
public class CmriIoActivator implements LayoutIoActivator
{
    public final static String  PLAIN_NAME_STRING = "cmri";

    public CmriIoActivator()
    {
        // public constructor so loader can call newInstance() on class-ref.
    }

    @Override
    public void start( BundleContext context )
    {
        LOG.log( Level.INFO, "in CmriIoActivator#start(BC)" );
        LOG.log( Level.INFO, ".. our class loader is {0}", this.getClass().getClassLoader().getClass().getName() );

        m_bc = context;

        LayoutIoProviderManager  mgr = LayoutIoProviderManager.getInstance();
        m_the_provider = new CmriLayoutProviderImpl();
        mgr.addProvider( PLAIN_NAME_STRING, m_the_provider.getLongDescription(), m_the_provider );
    }

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
     */
    transient private CmriLayoutProviderImpl   m_the_provider;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriIoActivator.class.getName() );

}
