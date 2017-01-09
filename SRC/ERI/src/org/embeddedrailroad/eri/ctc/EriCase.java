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

package org.embeddedrailroad.eri.ctc;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.*;

import org.embeddedrailroad.eri.layoutio.LayoutIoProviderManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoActivator;
import org.embeddedrailroad.eri.layoutio.LayoutIoTransport;
import org.embeddedrailroad.eri.layoutio.LayoutTimeoutManager;
import org.embeddedrailroad.eri.xml.BankBean;
import org.embeddedrailroad.eri.xml.BankListBean;
import org.embeddedrailroad.eri.xml.LayoutConfigurationBean;

import com.crunchynoodles.util.StringUtils;
import com.crunchynoodles.util.exceptions.UnsupportedKeyException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

// import static org.embeddedrailroad.eri.ctc.EriCase.INI_SECTION_PROVIDERS_NAME;
// import static org.embeddedrailroad.eri.ctc.EriCase.INI_SECTION_STARTUP_NAME;


/**
 *  Start-up system object that holds application-wide variables , i.e. "globals".
 *  This is a singleton object.
 *
 * @author brian
 */
public class EriCase {

    private EriCase()
    {
        m_auto_startup = false;
    }

    /***
     * static method to get instance
     * @return singleton instance of manager
     * @see http://stackoverflow.com/questions/18093735/double-checked-locking-in-singleton
     */
    public static EriCase getInstance()
    {
        EriCase inst = EriCase.s_instance;
        if (inst == null) { // first-time lock
            synchronized (EriCase.class) {
                inst = EriCase.s_instance;
                if (inst == null) {  // second-time lock
                    EriCase.s_instance = inst = new EriCase();
                }
            }
        }
        return inst;
    }

    //-------------------------  INI LOAD / STORE  ------------------------

    /***
     *  Load the program's INI file.  Read only once.
     * @param f {@link File} object connected to a file.
     * @throws IOException
     * @throws InvalidFileFormatException
     */
    public void loadIni( File f )
            throws IOException, InvalidFileFormatException
    {
        synchronized( EriCase.class )
        {
            //  If haven't yet loaded settings, then create INI object and load.
            if( this.Ini == null )
            {
                this.Ini = new Ini();
                this.Ini.load( f );
            }
        }
    }

    /***
     *  Load the program's INI file.  Read only once.
     * @param ins {@link InputStream} object connected to INI source.
     * @throws IOException
     * @throws InvalidFileFormatException
     */
    public void loadIni( InputStream ins )
            throws IOException
    {
        synchronized( EriCase.class )
        {
            //  If haven't yet loaded settings, then create INI object and load.
            if( this.Ini == null )
            {
                this.Ini = new Ini();
                this.Ini.load( ins );
            }
        }
    }

    /***
     *  Load the program's INI file.  Read only once.
     * @param rdr {@link Reader} object connected to INI source.
     * @throws IOException
     * @throws InvalidFileFormatException
     */
    public void loadIni( Reader rdr )
            throws IOException
    {
        synchronized( EriCase.class )
        {
            //  If haven't yet loaded settings, then create INI object and load.
            if( this.Ini == null )
            {
                this.Ini = new Ini();
                this.Ini.load( rdr );
            }
        }
    }

    //---------------------------  RUN - DOIT  ----------------------------

    /***
     *  Pre-set the ERI application by loading a INI file that maps protocols to
     *  particular Java class in some JAR file.
     *
     * <p> Some protocol transports providers are compiled in.
     *  However, if "{@code jar=}" is provided, then the compiled-in code not looked at,
     *  instead using the external code.  Note, it is a failure if same-classes are overridden
     *  but not found.
     *
     * <p> Sample INIFile with two transportation providers.  The layout XML file is also specified.
     * <blockquote><pre>
    [providers]
    provider=cmri
    provider=cti

    [class.cmri]
    jar=dist/ERI.jar
    activator=org.embeddedrailroad.eri.layoutio.cmri.CmriIoActivator
    alias.1=C/MRI
    alias.2=CmriNet

    [class.cti]
    jar=dist/ERI.jar
    activator=org.embeddedrailroad.eri.layoutio.cti.CtiIoActivator

    [startup]
    layout=front_range_layout.xml
    startup=auto
</pre></blockquote>
     * <br>
     *  For INI4J help see: http://ini4j.sourceforge.net/tutorial/IniTutorial.java.html
     *
     * @param iniFilename INI file name, which must exist.
     *
     * @throws FileNotFoundException {@link iniFilename} not found, OR comms JAR file not found.
     * @throws InvalidFileFormatException Your INI file has a syntax error. :(
     * @throws ClassNotFoundException Comms Java class not found in JAR, or cannot start up
     *         dynamic-class OSGi {@link Framework} manager.
     */
    public void initialize( String iniFilename )
                throws FileNotFoundException, ClassNotFoundException,
                        InvalidFileFormatException
    {
        final String  _METHOD = "initialize";
        LOG.entering( "EriCase", _METHOD, iniFilename );

        try
        {
            loadIni( new File ( iniFilename ) );
        }
        catch( InvalidFileFormatException e2 )
        {
            LOG.log( Level.WARNING, "Your INI file has a syntax error", e2 );
            throw e2;
        }
        catch( IOException ex )
        {
            LOG.log( Level.WARNING, "Sorry, " + _METHOD + " cannot read your INI file", ex );
            throw new FileNotFoundException( ex.getMessage() );
        }

        theTransportManager = LayoutIoProviderManager.getInstance();

        //  Start up OSGI framework, so we can load external LayoutIO Transports.
        final String  loader_name = "com.crunchynoodles.osgi.FakeOSGiFrameworkFactory" ;
        try
        {
            launchOSGi( loader_name, new File[1] );
        }
        catch( Exception ex )
        {
            LOG.log( Level.SEVERE, "Failed to start OSGi Framework loader", ex );
            throw new ClassNotFoundException( "Failed to start OSGi Framework loader", ex );
        }

        Ini.Section  provider_section = Ini.get( INI_SECTION_PROVIDERS_NAME );
        List<String>  provider_list = provider_section.getAll( INI_KEY_PROVIDERS_PROVIDER_NAME );

        for( String prov : provider_list )
        {
            //  Skip duplicate transport providers in user's INI file.
            if( theTransportManager.findProviderByName( prov ) != null )
            {
                continue;
            }

            //  Find section e.g. "[class.cti]"
            final String  provider_section_name = "class." + prov;

            Ini.Section  provider_sect = Ini.get ( provider_section_name );
            if( provider_sect == null )
            {
                LOG.log( Level.WARNING, "No \"[{0}]\" section in INI file.", provider_section_name );
                continue;
            }

            LOG.log( Level.INFO, "Looking for \"{0}\" activator", provider_section_name );
            String  jar_place = provider_sect.fetch( INI_KEY_CLASS_JAR );
            String  impl_full_class = provider_sect.fetch( INI_KEY_BUNDLE_ACTIVATOR );

            ClassLoader  cl = null;
            Class<?>  jarred = null;
            try
            {
                //  Check externally if "jar=" specified.
                if( jarred == null && !StringUtils.emptyOrNull( jar_place ) )
                {
                    //  Dynamically loading can override compile-in classes.
                    //  Uses URLClassLoader, therefore it leaves external to base application.
                    File  myJarFile = new File( jar_place );
                    if (!myJarFile.isFile()) {
                      throw new FileNotFoundException("Missing required JAR: " + myJarFile.toString());
                    }

                    //  Feed reference to a new class-loader.  'cl' is the ref-count anchor point for
                    //  the loaded class.  It and the BundleContext keep it from being GC'ed.
                    //  http://stackoverflow.com/questions/148681/unloading-classes-in-java
                    final URI  myJarUrl = myJarFile.toURI();
                    cl = URLClassLoader.newInstance(new URL[]{ myJarUrl.toURL() });

                    jarred = cl.loadClass( impl_full_class );
                }

                //  If not found yet and no "jar=" clause, then try internally.
                //  Note, it is an error if a built-in provider is overridden but *.code file not found.
                if( jarred == null && StringUtils.emptyOrNull( jar_place ) )
                {
//                    //  Use SystemClassLoader, therefore support classes located within base application..
//                    cl = ClassLoader.getSystemClassLoader();
//                    try
//                    {
//                        if( (jarred = cl.loadClass( impl_full_class )) != null )
//                        {
//                            LOG.log( Level.INFO, "Good news, class \"{0}\" is built-in!", impl_full_class );
//                        }
//                    }
//                    catch( ClassNotFoundException ex )
//                    {
//                        //  'impl_full_class' not found, which means clas isn't compiled-in.
//                    }

                    try
                    {
                        jarred = Class.forName( impl_full_class );
                    }
                    catch( ClassNotFoundException ex )
                    {
                        //  'impl_full_class' not found, which means class isn't compiled-in.
                    }
                }

                //  Did any class-file search succeed?  If yes, then start its Activator.
                if( jarred != null )
                {
                    Object  prov_obj = jarred.newInstance();

                    if( prov_obj instanceof LayoutIoActivator )
                    {
                        final LayoutIoActivator  activator = (LayoutIoActivator) prov_obj;

                        //  Create a fake/place-holder bundle context.
                        //!! BundleContext fake_bc = new FakeOSGiBundleContext( activator );

                        BundleContext fake_bc = null;

                        // Start it up by calling "void start(BundleContext fake_ctx)"
                        @SuppressWarnings("unchecked")
                        Method   m = jarred.getMethod( "start", new Class[]{ BundleContext.class } );

                        Object  rv = m.invoke( activator, fake_bc );
                        // see http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html
                        // see http://frankkieviet.blogspot.com/2006/10/how-to-fix-dreaded-permgen-space.html

                        System.out.println( "YES!" );

                        //  Remember it, so we'll keep reference to class-loader and activator.
                        ActivationStruct  as = new ActivationStruct( cl, activator, fake_bc, provider_section_name );
                        m_activatorList.add( as );
                    }
                    else
                    {
                        //  YUCK, UNLOAD THAT CLASS!!

                        prov_obj = null;
                        jarred = null;

                        // Allow 'finally' block to close class-loader...
                    }
                }
            }
            catch (ClassNotFoundException e1) {
                LOG.log( Level.WARNING, "Sorry, \"{0}\" is not available, can't find Activator class", prov );
            }
            catch( NoSuchMethodException|InstantiationException e2 )
            {
                LOG.log( Level.WARNING, "Sorry, \"{0}\" is not available, can't run #start() method", prov );
            }
            catch( Exception ex )
            {
                //  LOG.log() will use "{0}" to pick up one parameter, but "{1}" means nothing.
                //  If have two parmaters to message-string, use String.format().  It don't seem correct.
                //  -- BWitt, Sept 2014
                LOG.log( Level.WARNING, String.format( "Sorry, can't get \"%1$s\" provider loaded: %2$s",
                                                        prov, ex.getMessage() ) );
            }
            finally
            {
                if( cl != null && cl instanceof Closeable )
                {
                    try {
                        ((Closeable)cl).close();
                    }
                    catch( IOException _ignore ) {
                        LOG.log( Level.WARNING, "cl.close() failed: {0}", _ignore.getMessage() );
                    }
                }
                cl = null;
            }
        }

        //  If there were classes we don't want any more, finalize and GC.
        //  The Squeeze Inn.
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().gc();

        //  If automatic startup, then set for all comms providers polling their units.
        m_auto_startup = false;
        Ini.Section  startup_section = Ini.get( INI_SECTION_STARTUP_NAME );
        if( startup_section != null )
        {
            String how_startup = startup_section.fetch( INI_KEY_STARTUP_NAME );
            if( how_startup != null && how_startup.equalsIgnoreCase( "auto" ) )
            {
                m_auto_startup = true;
            }
        }

        LOG.exiting( "EriCase", _METHOD );
    }

    /***
     *  Create all transports as specified in the train-layout XML file, utilizing the
     *  providers given in our INI file.
     *  On success, returns ; otherwise throws an exception with explanation of trouble.
     *
     * @param layoutXmlFilename file name for XML bank and layout description.
     * @throws java.lang.Exception trouble.
     */
    public void createTransports( String layoutXmlFilename )
            throws Exception
    {
        LayoutConfigurationBean  bs;
        try
        {
            bs = LayoutConfigurationBean.readFromFile( layoutXmlFilename );
            if( null == bs )
            {
                throw new Exception( "empty Layout Configuratio" );
            }

            final LayoutIoProviderManager mgr = LayoutIoProviderManager.getInstance();
            LayoutIoProviderManager.ProviderTransportStruct  tranStruct;
            LayoutIoTransport  trans;

            BankListBean  banklist = bs.getBankList();
            for( BankBean bank : banklist.getBankList() )
            {
                tranStruct = mgr.findProviderByName( bank.getProtocol() );
                // Out( "... bank #%s %s = \"%s\"", bank.getAddress(), BankBean.ATTR_PROTOCOL, bank.getProtocol() );
                trans = tranStruct.provider.makeChannel( bank.getPhysical(), Integer.parseInt( bank.getAddress() ) );

                trans.setProperties( bank.getComms().values() );

                trans.attach();
            }
        }
        catch( UnsupportedKeyException ex )
        {
            LOG.log( Level.SEVERE, "Transport rejects property key \"{0}\"", ex.getMessage() );
            throw ex;
        }

    }

    /***
     *  At shutdown time, stop all the protocol-transports and detach/release their comms-channel.
     */
    public void destoryTransports()
    {
        final LayoutIoProviderManager mgr = LayoutIoProviderManager.getInstance();

        Collection<LayoutIoProviderManager.ProviderTransportStruct>  transList = mgr.getProviderTransportList().values();
        for( LayoutIoProviderManager.ProviderTransportStruct  tranStruct : transList )
        {
            Collection<LayoutIoTransport>  provList = tranStruct.provider.getTransportChannelList().values();
            for( LayoutIoTransport trans : provList )
            {
                try
                {
                    LOG.log( Level.INFO, String.format( "Stopping %1$s #%2$d ...", trans.getProtocolName(), 1 ) );

                    trans.setPolling( false );
                    trans.detach();
                }
                catch( Exception ex )
                {
                    LOG.log( Level.WARNING, "transport shutdown aborted.", ex );
                }
            }
        }
    }

    /***
     *  Launch the OSGi Framework to load Layout IO Transports.
     *  Framework is loaded and started up.
     *
     * @see "osgi.core-6.0.0.pdf", section 4.2.1"
     * @param factoryName
     * @param bundles
     * @throws Exception
     */
    void launchOSGi( String factoryName, File[] bundles )
                    throws Exception
    {
        Map<String,String>  p = new HashMap<String, String>();
        p.put( org.osgi.framework.Constants.FRAMEWORK_STORAGE,
                System.getProperty("user.home")
                                + File.separator + "osgi" );
        FrameworkFactory factory = (FrameworkFactory) Class.forName( factoryName ).newInstance();
        m_framework = factory.newFramework(p);
        m_framework.init();

        BundleContext context = m_framework.getBundleContext();

        for ( File bundle : bundles )
            context.installBundle( bundle.toURL().toString() );

        m_framework.start();

        // framework.waitForStop(0);
    }

    // -----------------------------  Start Your Engines!  -----------------------------

    /***
     *  Run the transports and let the models be alive!
     */
    public void doit()
    {
        LOG.entering( "EriCase", "doit" );

        if( m_auto_startup )
        {
            final LayoutIoProviderManager mgr = LayoutIoProviderManager.getInstance();

            Collection<LayoutIoProviderManager.ProviderTransportStruct>  transList = mgr.getProviderTransportList().values();
            for( LayoutIoProviderManager.ProviderTransportStruct  tranStruct : transList )
            {
                Collection<LayoutIoTransport>  provList = tranStruct.provider.getTransportChannelList().values();
                for( LayoutIoTransport trans : provList )
                {
                    trans.setPolling( true );
                }
            }
        }

        LOG.exiting( "EriCase", "doit" );
    }

    /***
     *  Much of the logic for ERI is timer based, call here to stop that stuff.
     */
    public void shutdownTimers()
    {
        LayoutTimeoutManager.getInstance().shutdownNow();

        //  Stop the bundles of the framework, and the framework itself.
        //  If framework never really started, then it will throw exceptions on the way down.
        try { m_framework.stop(); } catch(BundleException be) { }
        try { m_framework.waitForStop(0); } catch(InterruptedException ie) { }
    }

    //--------------------------  INSTANCE VARS  --------------------------

    /*** Section name listing OSGi LayoutIo providers.  Each provider is detailed in its own section, later. */
    public static final String    INI_SECTION_PROVIDERS_NAME = "providers";

    /***
     *  Each provider uses this key inside section {@link INI_SECTION_PROVIDERS_NAME}.
     *  Key is repeated for each provider detailed in the INI file.
     */
    public static final String    INI_KEY_PROVIDERS_PROVIDER_NAME = "provider";

    /***
     *  In "[class.cmri]" section, the optional "jar=" clause specifies a named file wherein
     *  the Activator class can be found.
     *  However, if class is built-in to the ERI application, this clause is ignored.
     */
    public static final String    INI_KEY_CLASS_JAR = "jar";

    /***
     *  Key to find the implementation class to activate the bundle where the
     *  communication protocol lives.
     *  The class binary-name can be built-in (ignoring <tt>jar=</tt>) or outside in a file.
     *
     *  <p> See http://www.jroller.com/habuma/entry/a_dozen_osgi_myths_and , "OSGi is too heavyweight"
     *
     */
    public static final String    INI_KEY_BUNDLE_ACTIVATOR = "activator";

    /***
     *  Section about what to do once system initialized, e.g. (1) start polling immediately
     *  and (2) which layout description to load otherwise prompt user.
     */
    public static final String    INI_SECTION_STARTUP_NAME = "startup";

    /*** Key that has layout XML file, within section {@link INI_SECTION_STARTUP_NAME}. */
    public static final String    INI_KEY_LAYOUT_NAME = "layout";

    /*** Key that has describes how to start-up, either "auto", "no" or "yes". */
    public static final String    INI_KEY_STARTUP_NAME = "startup";

    /*** Singleton holder of all Layout IO providers. */
    public LayoutIoProviderManager   theTransportManager = null;

    //-----------------  FakeOSGiBundleContext Handy Data  ----------------

    class ActivationStruct
    {
        public ClassLoader          m_class_loader;
        public LayoutIoActivator    m_activator;
        public BundleContext        m_context;
        public String               m_class;

        public ActivationStruct( ClassLoader cl, LayoutIoActivator act, BundleContext bc, String className )
        {
            this.m_class_loader = cl;
            this.m_activator = act;
            this.m_context   = bc;
            this.m_class     = className;
        }
    }

    //-----------------------  PRIVATE INSTANCE VARS  ---------------------

    private static volatile EriCase    s_instance;

    /***  Logging spigot. */
    private static transient final Logger LOG = Logger.getLogger( EriCase.class.getName() );

    /*** Global INI file sections, key and values. */
    protected Ini      Ini = null;

    /*** Known {@link LayoutIoActivator} providers. */
    private List<ActivationStruct>   m_activatorList = new ArrayList<ActivationStruct>();

    /*** Should polling begin whenever program is started up? */
    transient protected boolean        m_auto_startup;

    /*** OSGi Framework to load external Layout IO Transports. */
    transient protected Framework      m_framework;

}
