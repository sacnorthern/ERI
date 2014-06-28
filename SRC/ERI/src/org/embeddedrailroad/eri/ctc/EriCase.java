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
import java.util.logging.LogManager;
import java.util.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;

import org.embeddedrailroad.eri.layoutio.IoTransportManager;
import org.embeddedrailroad.eri.layoutio.LayoutIoProvider;
import org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl;
import org.ini4j.*;
import sun.tools.jar.resources.jar;


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
            throws IOException
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

    public void doit()
    {
        LOG.entering( "EriCase", "doit" );

        LogManager  lm = LogManager.getLogManager();
        Enumeration<String>  namelist = lm.getLoggerNames();

        System.out.println( "Found these loggers:" );
        while( namelist.hasMoreElements() )
        {
            String  name = namelist.nextElement();
            System.out.printf( "  logger = %s\n", name );

        }

        Logger.getGlobal().setLevel( Level.ALL );

        try
        {
            loadIni( new File (INI_FILENAME_DEFAULT) );
        }
        catch( IOException ex )
        {
            LOG.log( Level.WARNING, "Sorry, doit cannot open INI file", ex );
        }

        TransportManager = IoTransportManager.getInstance();

        Ini.Section  section = Ini.get( INI_SECTION_PROVIDERS_NAME );
        List<String>  provider_list = section.getAll( INI_KEY_PROVIDERS_PROVIDER_NAME );

        System.out.println( "INI uses these providers:" );
        for( String prov : provider_list )
        {
            try
            {
                System.out.printf( "   %s\n", prov );
                if( TransportManager.findProviderByName( prov ) == null )
                {
                    if( prov.equalsIgnoreCase( "cmri") )
                    {
                        File  myJarFile = new File("dist/ERI.jar");
                        if (!myJarFile.isFile()) {
                          throw new FileNotFoundException("Missing required JAR: " + myJarFile.toString());
                        }
                        URI  myJarUrl = myJarFile.toURI();

                        URLClassLoader  cl = URLClassLoader.newInstance(new URL[]{myJarUrl.toURL()});

                        Class jarred = null;
                        jarred = cl.loadClass("org.embeddedrailroad.eri.layoutio.cmri.CmriLayoutProviderImpl");

                        //  This call gets the class-static code-block to run, which causes
                        //  the provider to register itself.
                        Object  prov_obj = jarred.newInstance();

                        if( prov_obj instanceof LayoutIoProvider )
                        {
                            System.out.println( "YES!" );
                        }
                    }
                    else
                    {
                        LOG.log(  Level.WARNING, "\"{0}\" not found.", prov );
                    }
                }
            }
            catch (ClassNotFoundException e) {
                            System.out.println("2");
                            e.printStackTrace();
            }
            catch( Exception ex )
            {
                LOG.log( Level.WARNING, "Sorry, can't get \"" + prov + "\" transport loaded", ex );
            }
        }

        LOG.exiting("EriCase", "doit" );
    }

    //--------------------------  INSTANCE VARS  --------------------------

    public static String    INI_FILENAME_DEFAULT = "my_eri.ini";
    public static String    INI_SECTION_PROVIDERS_NAME = "providers";
    public static String    INI_KEY_PROVIDERS_PROVIDER_NAME = "provider";

    public Ini      Ini = null;

    public IoTransportManager   TransportManager = null;

    //-----------------------  PRIVATE INSTANCE VARS  ---------------------

    private static volatile EriCase  s_instance;

    private static final Logger LOG = Logger.getLogger( EriCase.class.getName() );
}
