/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.appl;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.*;

import com.crunchynoodles.util.PropertiesManager;
import com.crunchynoodles.util.UserDirectories;
import org.embeddedrailroad.eri.xml.BankBean;
import org.embeddedrailroad.eri.xml.BankListBean;
import org.embeddedrailroad.eri.xml.LayoutConfigurationBean;

/**
 *
 * @author brian
 */
public class EriApplication
{

    static PropertiesManager     props = new PropertiesManager("appl");

    private static final Logger logger = Logger.getLogger( EriApplication.class.getName() );

    //! public static LogManager           s_logmgr = LogManager.getLogManager();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        UserDirectories.CompanyName = "CrunchyNoodles";
        UserDirectories.ApplicationName = "EriApplication";

        //! logger = s_logmgr.getLogger( EriApplication.class.getName() );
        logger.info( "Hello ERI logger!" );

        String propfname = UserDirectories.getInstance().getUserApplSettingsPropertiesFilename();

        Out( "user-wide properties fname = \"%s\"", propfname);

        PropertiesManager.ApplicationName = UserDirectories.ApplicationName;
        props.readProperties( propfname, UserDirectories.getInstance().getProjectApplSettingsFolder() );


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

                props.tempPut( key, val );
            }
        }

        test2001();

        // Start up the application for real...
    }

    // ----------------------------------------------------------------------------

    static void test2001()
    {
        OutTestHeader( "Test2001" );

        String  fname = "C:\\Devel\\Java-ERI\\Deployment\\front_range_layout.xml";

        try {
            LayoutConfigurationBean  bs = LayoutConfigurationBean.readFromFile( fname );

            BankListBean  banklist = bs.getBankList();
            for( BankBean bank : banklist.getBankList() )
            {
                Out( "... bank #%s %s = \"%s\"", bank.getAddress(), BankBean.ATTR_PROTOCOL, bank.getProtocol() );
            }

            Out( "LayoutConfigurationBean#hashCode() = 0x%X\n", bs.hashCode() );

            if( ! bs.equals( bs ) )
            {
                Out( "ERROR: %s is NOT equal to itself!\n", bs.getClass().getName() );
            }

        } catch( Exception ex ) {
            System.out.printf("LayoutConfigurationBean.readFrom( \"%s\" ) failed ", fname);
            ex.printStackTrace();
        }

    }

    // ----------------------------------------------------------------------------

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
