/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import com.crunchynoodles.util.StringUtils;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;

import org.embeddedrailroad.eri.layoutio.LayoutIoProviderManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoActivator;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.cmri.CmriIoActivator;
import org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl;
import org.ini4j.*;
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
        if (s_instance == null) { // first time lock
            synchronized (EriCase.class) {
                if (s_instance == null) {  // second time lock
                    s_instance = new EriCase();
                }
            }
        }
        return s_instance;
    }

    //-------------------------  INI LOAD / STORE  ------------------------

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
     *  However, if "{@code jar=} is provided, then the compiled-in code not looked at,
     *  instead using the external code.  Note, it is a failure if same-classes are overridden
     *  but not found.
     *
     * <p> Sample INIFile with two transportation providers.  The layout XML file is also specified.
     * <pre>
    [providers]
    provider=cmri
    provider=cti

    [class.cmri]
    jar=dist/ERI.jar
    activator=org.embeddedrailroad.eri.layoutio.cmri.CmriIoActivator
    alias.1=C/MRI

    [class.cti]
    jar=dist/ERI.jar
    activator=org.embeddedrailroad.eri.layoutio.cti.CtiIoActivator


    [startup]
    layout=front_range_layout.xml
    startup=auto
</pre>
     *
     * @param iniFilename INI file name, which must exist.
     *
     * @throws FileNotFoundException {@link iniFilename} not found, OR comms JAR file not found.
     * @throws InvalidFileFormatException Your INI file has a syntax error. :(
     * @throws ClassNotFoundException Comms Java class not found in JAR.
     */
    public void initialize( String iniFilename )
                throws FileNotFoundException, ClassNotFoundException,
                        InvalidFileFormatException
    {
        LOG.entering( "EriCase", "initialize" );

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
            LOG.log( Level.WARNING, "Sorry, initialize() cannot read your INI file", ex );
            throw new FileNotFoundException( ex.getMessage() );
        }

        theTransportManager = LayoutIoProviderManager.getInstance();

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
            String  provider_section_name = "class." + prov;

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
            Class    jarred = null;
            try
            {
                //  Check externally if "jar=" specified.
                if( jarred == null && !StringUtils.emptyOrNull( jar_place ) )
                {
                    //  Dynamically loading can override compile-in classes.
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
                    cl = ClassLoader.getSystemClassLoader();
                    try
                    {
                        if( (jarred = cl.loadClass( impl_full_class )) != null )
                        {
                            LOG.log( Level.INFO, "Good news, class \"{0}\" is built-in!", impl_full_class );
                        }
                    }
                    catch( ClassNotFoundException ex )
                    {
                        //  'impl_full_class' not found, which means clas isn't compiled-in.
                    }
                }

                //  Did any search succeed?  If yes, then start Activator.
                if( jarred != null )
                {
                    Object  prov_obj = jarred.newInstance();

                    if( prov_obj instanceof LayoutIoActivator )
                    {
                        final LayoutIoActivator  activator = (LayoutIoActivator) prov_obj;

                        //  Create a fake/place-holder bundle context.
                        FakeOSGiBundleContext fake_bc = new FakeOSGiBundleContext( activator );

                        // Start it up by calling "void start(BundleContext fake_ctx)"
                        @SuppressWarnings("unchecked")
                        Method  m = jarred.getMethod( "start", new Class[]{ BundleContext.class } );

                        Object rv = m.invoke( activator, fake_bc );
                        // see http://frankkieviet.blogspot.com/2006/10/classloader-leaks-dreaded-permgen-space.html
                        // see http://frankkieviet.blogspot.com/2006/10/how-to-fix-dreaded-permgen-space.html

                        System.out.println( "YES!" );

                        //  Remember it, so we'll keep reference to class-loaer and activator.
                        ActivationStruct  as = new ActivationStruct( cl, activator, fake_bc, provider_section_name );
                        m_activatorList.add( as );
                    }
                    else
                    {
                        //  YUCK, UNLOAD THAT CLASS!!

                        prov_obj = null;
                        jarred = null;

                        if( cl instanceof Closeable )
                            ((Closeable)cl).close();
                        cl = null;
                    }
                }
            }
            catch (ClassNotFoundException e) {
                LOG.log( Level.WARNING, "Sorry, \"{0}\" is not available, can't find Activator class", prov );
            }
            catch( Exception ex )
            {
                String  base_class_name = ex.getMessage();
                LOG.log( Level.WARNING, String.format( "Sorry, can't get \"%1$s\" provider loaded: %2$s",
                                                        prov, base_class_name ) );
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

        LOG.exiting( "EriCase", "initialize" );
    }

    public void doit()
    {
        LOG.entering( "EriCase", "doit" );
        if( m_auto_startup )
        {
            // TODO: Tell all providers to start running their transport IO banks.
        }
        LOG.exiting( "EriCase", "doit" );
    }

    //--------------------------  INSTANCE VARS  --------------------------

    public static final String    INI_SECTION_PROVIDERS_NAME = "providers";
    public static final String    INI_KEY_PROVIDERS_PROVIDER_NAME = "provider";

    /***
     *  In "[class.cmri]" section, the optional "jar=" clause specifies a named file wherefore
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
    public static final String    INI_KEY_LAYOUT_NAME = "layout";
    public static final String    INI_KEY_STARTUP_NAME = "startup";

    public Ini      Ini = null;

    public LayoutIoProviderManager   theTransportManager = null;

    //-----------------------  FAKE OSGi BUNDLE CONTEXT  ----------------------

    class FakeOSGiBundleContext implements BundleContext
    {
        public  Object  m_reference;

        public FakeOSGiBundleContext( Object obj )
        {
            m_reference = obj;
        }

        @Override
        public String getProperty( String string ) {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Bundle getBundle() {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
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
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ServiceRegistration<?> registerService( String string, Object o, Dictionary<String, ?> dctnr ) {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <S> ServiceRegistration<S> registerService( Class<S> type, S s, Dictionary<String, ?> dctnr ) {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <S> ServiceRegistration<S> registerService( Class<S> type, ServiceFactory<S> sf, Dictionary<String, ?> dctnr ) {
            throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
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

    //-----------------------  PRIVATE INSTANCE VARS  ---------------------

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

    protected List<ActivationStruct>   m_activatorList = new ArrayList<ActivationStruct>();

    transient protected boolean        m_auto_startup;

    /***  Logging spigot. */
    private static transient final Logger LOG = Logger.getLogger( EriCase.class.getName() );
}
