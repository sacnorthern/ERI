/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio.cmri;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.embeddedrailroad.eri.layoutio.IoTransportManager;
import org.osgi.framework.BundleContext;

import org.embeddedrailroad.eri.layoutio.LayoutIoActivator;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;

/**
 *  Starts up or stops a layout communications CMRI provider.
 *  There will be only one instance of each protocol-activator created (though not enforced).
 *  Starting registers with {@link IoTransportManager}.
 *  Modeled after OSGi framework.
 * <br/>
 *      http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleActivator.html
 *
 * @author brian
 */
public class CmriIoActivator implements LayoutIoActivator
{
    public CmriIoActivator()
    {
        // public constructor so loader can call newInstance() on class-ref.
    }

    @Override
    public void start( BundleContext context )
    {
        m_bc = context;

        LOG.log( Level.INFO, "in CmriIoActivator#start(BC)" );
        IoTransportManager  mgr = IoTransportManager.getInstance();
        m_the_provider = new CmriLayoutProviderImpl();
        mgr.addProvider( "cmri", "The CMRI protocol", m_the_provider );
    }

    @Override
    public void stop( BundleContext context )
    {
        // TODO: Close down all transports.

        m_the_provider = null;
        IoTransportManager  mgr = IoTransportManager.getInstance();
        mgr.removeProviderTransport( "cmri" );
        LOG.log( Level.INFO, "in CmriIoActivator#stop(BC)" );

        m_bc = null;
    }

    @Override
    public String versionValue()
    {
        return "0.0.1 build 1";
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
    private BundleContext       m_bc;

    /***
     *  Provider is a singleton.
     */
    private CmriLayoutProviderImpl   m_the_provider;

    /***  Logging output spigot. */
    private static final Logger LOG = Logger.getLogger( CmriIoActivator.class.getName() );

}
