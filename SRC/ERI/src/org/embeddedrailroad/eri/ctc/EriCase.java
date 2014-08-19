/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;

import org.embeddedrailroad.eri.layoutio.IoTransportManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl;
import org.ini4j.*;


/**
 *  Start-up system object that holds application-wide variables , i.e. "globals".
 *  This is a singleton object.
 *
 * @author brian
 */
public class EriCase {

    private EriCase()
    {
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
     *  Sample INIFile with two transportation providers.  The layout XML file is also specified.
     * <pre>
    [providers]
    provider=cmri
    provider=cti

    [class.cmri]
    jar=dist/ERI.jar
    impl=org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl
    alias.1=C/MRI

    [class.cti]
    jar=dist/ERI.jar
    impl=org.embeddedrailroad.eri.layoutio.cti.CtiLayoutProviderImpl


    [startup]
    layout=front_range_layout.xml
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
        LOG.entering( "EriCase", "doit" );

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

        theTransportManager = IoTransportManager.getInstance();

        Ini.Section  section = Ini.get( INI_SECTION_PROVIDERS_NAME );
        List<String>  provider_list = section.getAll( INI_KEY_PROVIDERS_PROVIDER_NAME );

        System.out.println( "INI uses these providers:" );
        for( String prov : provider_list )
        {
            String  provider_section_name = "class." + prov;
            LOG.log( Level.INFO, "Looking for \"{0}\" transport", provider_section_name );

            Ini.Section  prov_sect = Ini.get ( provider_section_name );
            String  jar_place = prov_sect.get( INI_KEY_CLASS_JAR );
            String  impl_full_class = prov_sect.get( INI_KEY_CLASS_IMPLMENTATION );

            try
            {
                if( theTransportManager.findProviderByName( prov ) == null )
                {
                    //  If not yet known, then load it dynamically.
                    //!! if( prov.equalsIgnoreCase( "cmri") )
                {
                    File  myJarFile = new File( jar_place );  //!! ("dist/ERI.jar");
                    if (!myJarFile.isFile()) {
                      throw new FileNotFoundException("Missing required JAR: " + myJarFile.toString());
                    }

                    final URI  myJarUrl = myJarFile.toURI();
                    URLClassLoader  cl = URLClassLoader.newInstance(new URL[]{ myJarUrl.toURL() });

                    Class jarred;
                    jarred = cl.loadClass( impl_full_class );  //!! ("org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl");

                    //  This call gets the class-static code-block to run, which causes
                    //  the provider to register itself.
                    Object  prov_obj = jarred.newInstance();

                    if( prov_obj instanceof LayoutIoProvider )
                    {
                        System.out.println( "YES!" );
                    }
                }
                //!! else
                //!! {
                //!!     LOG.log(  Level.WARNING, "\"{0}\" not found.", prov );
                //!! }
                }
            }
            catch (ClassNotFoundException e) {
                System.out.println("2");
                e.printStackTrace();
                throw e;
            }
            catch( Exception ex )
            {
                LOG.log( Level.WARNING, "Sorry, can't get \"" + prov + "\" transport loaded", ex );
            }
        }

        LOG.exiting("EriCase", "doit" );
    }

    //--------------------------  INSTANCE VARS  --------------------------

    public static String    INI_SECTION_PROVIDERS_NAME = "providers";
    public static String    INI_KEY_PROVIDERS_PROVIDER_NAME = "provider";

    public static String    INI_KEY_CLASS_JAR = "jar";

    public static String    INI_KEY_CLASS_IMPLMENTATION = "impl";

    public Ini      Ini = null;

    public IoTransportManager   theTransportManager = null;

    //-----------------------  PRIVATE INSTANCE VARS  ---------------------

    private static volatile EriCase  s_instance;

    private static final Logger LOG = Logger.getLogger( EriCase.class.getName() );
}
