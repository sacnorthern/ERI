/***  Java-ERI    Java-based Embedded Railroad Interfacing.
 ***  Copyright (C) 2014, 2016 in USA by Brian Witt , bwitt@value.net
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

 /***
  *     This is the main for the ERI application.
  */

package org.embeddedrailroad.appl;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;

import com.crunchynoodles.util.PropertiesManager;
import com.crunchynoodles.util.UserDirectories;
import java.io.IOException;

import org.embeddedrailroad.eri.ctc.EriCase;
import org.embeddedrailroad.eri.layoutio.LayoutIoProviderManager;
import org.embeddedrailroad.eri.xml.BankBean;
import org.embeddedrailroad.eri.xml.BankListBean;
import org.embeddedrailroad.eri.xml.LayoutConfigurationBean;

/**
 *
 * @author brian
 */
public class EriApplication
{
    /***   Default file name for layout (INI file). */
    public static String    INI_FILENAME_DEFAULT = "my_eri.ini";

    static transient PropertiesManager     s_props = new PropertiesManager("appl");
    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( EriApplication.class.getName() );

    /***
     *  Start up the Embedded Railroad Interface (ERI) server application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        UserDirectories.CompanyName = "org.embeddedrailroad";
        UserDirectories.ApplicationName = "EriApplication";

        //  Check out loggers available.
//        LogManager  lm = LogManager.getLogManager();
//        Enumeration<String>  namelist = lm.getLoggerNames();
//
//        System.out.println( "Found these loggers:" );
//        while( namelist.hasMoreElements() )
//        {
//            String  name = namelist.nextElement();
//            System.out.printf( "  logger = \"%s\"\n", name );
//        }

        Logger.getGlobal().setLevel( Level.CONFIG );

        LOG.info( "Hello ERI logger!" );

        String propfname = UserDirectories.getInstance().getUserApplSettingsPropertiesFilename();

        LOG.log( Level.INFO, "user-wide properties fname = \"{0}\"", propfname);

        PropertiesManager.ApplicationName = UserDirectories.ApplicationName;
        s_props.readProperties( propfname, UserDirectories.getInstance().getProjectApplSettingsFolder() );

        String  fname = "C:\\Devel\\Java-ERI\\Deployment\\front_range_layout.xml";

        //  Parse command line looking for "/prop:KEY=VALUE" or "/property:KEY=VALUE" and add to our properties.
        //  Print usage for "/help" , "/?" or "--help".
        for( String option : args )
        {
            int  after_colon = -1;
            if( option.startsWith( "/prop:" ) || option.startsWith( "-prop:" ) )
            {
                after_colon = 6;
            }
            else if( option.startsWith( "/property:" ) || option.startsWith( "-property:" ) )
            {
                after_colon = 10;
            }

            if( after_colon > 0 )
            {
                //  Pick up option for this run and add to application properties.
                int  eq = option.indexOf( '=', after_colon );
                if( eq < 0 )
                {
                    Out( "ERROR: ill-formed property option {{%s}}", option );
                    System.exit(1);
                }

                String key = option.substring( after_colon, eq );
                String val = option.substring( eq +1 );

                s_props.tempPut( key, val );
            }
            else
            if( option.startsWith( "/help" ) ||
                option.startsWith( "/?" ) ||
                option.startsWith( "-h" ) ||
                option.startsWith( "-help" ) ||
                option.startsWith( "--help" ) )
            {
                Out( "Help for %s version %s by %s",
                        UserDirectories.ApplicationName,
                        "(unknown)",
                        UserDirectories.CompanyName );
                Out( "   /property:KEY=VALUE  -- set property value for program." );
                Out( "   /prop:KEY=VALUE      -- set property value for program." );

                Out( "   Logging level is " + LOG.getLevel() );

                Out( "Get help: /help /? -h -help --help" );
                Out( "" );

                LOG.log( Level.INFO, "Application exits...");
                return ;
            }
        }

        // ------------------------------------------------------
        //  Turn on assertion checking?  Good for strict debugging.
        //  Not on by default, must be explicit about value being "on".

        if( s_props.get( "assertions", "NOPE" ).equalsIgnoreCase( "on" ) )
        {
            ClassLoader  dcl = ClassLoader.getSystemClassLoader();
            dcl.setDefaultAssertionStatus( true );
            dcl.setClassAssertionStatus( "com.crunchynoodles", true );
            dcl.setClassAssertionStatus( "org.embeddedrailroad", true );
        }


        // TODO: If cmd-ine is "makeini", then create a sample INI file
        //      based on Activators built-in to the ERI executable.

        // ------------------------------------------------------
        //  Perform a few tests on components...
        //

        LOG.info( "Starting tests" );

        test2001( fname );

        test3001();

        // ------------------------------------------------------
        //  Start up the application for real...
        //

        EriCase   eri = EriCase.getInstance();

        try
        {
            eri.initialize( INI_FILENAME_DEFAULT );

            eri.createTransports( fname );
        }
        catch( Exception ex )
        {
            ex.printStackTrace( System.out );
            LOG.log(Level.WARNING, "Failed to startup and initialize: {0}", ex.getMessage());

            eri.shutdownTimers();

            System.exit( 2 );
        }

        eri.doit();

        // ------------------------------------------------------
        //  The party is over.  Time for everyone to go home...
        //

        try {
            System.out.print( "Hit return to shutdown: " );
            System.in.read( new byte[] { 1 } );
        }
        catch( IOException ignored ) { }

        eri.shutdownTimers();
        eri.destoryTransports();

        LOG.log( Level.WARNING, "Application exits...");
    }

    // ----------------------------------------------------------------------------

    static void test2001( String fname )
    {
        OutTestHeader( "Test2001" );

        try {
            LayoutConfigurationBean  bs = LayoutConfigurationBean.readFromFile( fname );
            if( null == bs )
            {
                Out( "LayoutConfigurationBean is null : Sorry, file <%s> read failed.", fname );
                return ;
            }

            Out( "======" );
            BankListBean  banklist = bs.getBankList();
            for( BankBean bank : banklist.getBankList() )
            {
                Out( "... bank #%s %s = \"%s\"", bank.getAddress(), BankBean.ATTR_PROTOCOL, bank.getProtocol() );
            }

            Out( "LayoutConfigurationBean#hashCode() = 0x%X\n", bs.hashCode() );

            Out( bs.toString() );

            if( ! bs.equals( bs ) )
            {
                Out( "ERROR: %s is NOT equal to itself!\n", bs.getClass().getName() );
            }

        } catch( Exception ex ) {
            Out( ex, "LayoutConfigurationBean.readFrom( \"%s\" ) failed ", fname );
        }

    }

    // ----------------------------------------------------------------------------

    static void test3001()
    {
        OutTestHeader( "Test3001" );

        Out( "These providers are available:%n------------------------------" );

        Collection<LayoutIoProviderManager.ProviderTransportStruct>  values = LayoutIoProviderManager.getInstance().getProviderTransportList().values();
        for( LayoutIoProviderManager.ProviderTransportStruct prov : values )
        {
            Out( "%13s : %s , version \"%s\"%n<description>%s%n</description>",
                    prov.shortName, prov.provider.getProtocolName(), prov.provider.getVersionString(),
                    prov.longDescr );
        }
        Out( "" );
    }

    // ----------------------------------------------------------------------------

    // ----------------------------------------------------------------------------

    public static void PrintStringList( String heading, List<String> strs )
    {
        Out( "--- %s ---", heading );
        for( String s : strs )
        {
            Out( "  {{%s}}", s );
        }
    }

    public static void PrintMapStringString( Map<String,String> mapping )
    {
        Set<String>  keys = mapping.keySet();
        for( String k : keys )
        {
            System.out.printf( "... %s = %s\n", k, mapping.get( k ) );
        }
    }

    // ----------------------------------------------------------------------------

    public static void OutTestHeader( String name )
    {
        Out( "\n~~~~~~~~~~  %s  ~~~~~~~~~~\n", name );
    }

    public static void Out(Exception ex, String format, Object ... args)
    {
        System.out.printf(format, args);
        ex.printStackTrace( System.out );
        System.out.println();
    }

    public static void Out(String format, Object ... args)
    {
        System.out.printf(format, args);
        System.out.println();
    }

}
