/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.appl;

import java.util.logging.Logger;
import java.util.*;
import java.util.logging.Level;

import com.crunchynoodles.util.PropertiesManager;
import com.crunchynoodles.util.UserDirectories;

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
    public static String    INI_FILENAME_DEFAULT = "my_eri.ini";

    static transient PropertiesManager     s_props = new PropertiesManager("appl");

    private static transient final Logger  logger  = Logger.getLogger( EriApplication.class.getName() );

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

        logger.info( "Hello ERI logger!" );

        String propfname = UserDirectories.getInstance().getUserApplSettingsPropertiesFilename();

        logger.log( Level.INFO, "user-wide properties fname = \"{0}\"", propfname);

        PropertiesManager.ApplicationName = UserDirectories.ApplicationName;
        s_props.readProperties( propfname, UserDirectories.getInstance().getProjectApplSettingsFolder() );

        String  fname = "C:\\Devel\\Java-ERI\\Deployment\\front_range_layout.xml";

        //  Parse command line looking for "/prop:KEY=VALUE" or "/property:KEY=VALUE" and add to our properties.
        for( String option : args )
        {
            int  after_colon = -1;
            if( option.startsWith( "/prop:" ) )
            {
                after_colon = 6;
            }
            else if( option.startsWith( "/property:" ) )
            {
                after_colon = 10;
            }

            if( after_colon > 0 )
            {
                //  Pick up option for this run and add to application properties.
                int  eq = option.indexOf( '=', after_colon );
                if( eq < 0 )
                {
                    Out( "ERROR: ill-formed /property: option {{%s}}", option );
                    System.exit(1);
                }

                String key = option.substring( after_colon, eq );
                String val = option.substring( eq +1 );

                s_props.tempPut( key, val );
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

        logger.info( "Starting tests" );

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
            logger.log(Level.WARNING, "Failed to startup and initialize: {0}", ex.getMessage());
            System.exit( 2 );
        }

        eri.doit();

    }

    // ----------------------------------------------------------------------------

    static void test2001( String fname )
    {
        OutTestHeader( "Test2001" );

        try {
            LayoutConfigurationBean  bs = LayoutConfigurationBean.readFromFile( fname );
            if( null == bs )
            {
                Out( "Sorry, file read failed.");
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
                    prov.shortName, prov.provider.getName(), prov.provider.getVersionString(),
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
