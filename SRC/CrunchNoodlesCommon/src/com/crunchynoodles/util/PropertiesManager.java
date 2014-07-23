/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Allow modules within an application to manage their persistent settings.
 *
 * @author brian
 */
public class PropertiesManager {

    public final static String FONT_NAME = "font.family";         // e.g. "verdana" or "lucinda console"
    public final static String SIZE_INT = "font.size";          // e.g. "12"
    public final static String MAIN_WINDOW = "mainWindow";

    /***
     *   Used for filename of properties file.  Not used for naming properties in the file.
     */
    public static String    ApplicationName;

    /**
     * *
     * Where on disk to read user's properties, regardless of project.
     * {@see ApplicationName}
     */
    public static String    ApplPropertiesFilename;

    /***
     * Where on disk to read and write project-specific properties of the application.
     * {@see ApplicationName}
     */
    public static String    ProjectPropertiesFilename;

    /***
     *  Create property manager for application.  There are no properties until
     *  loaded with {@code readProperties()} is called.
     *
     * @param moduleName Prefix for properties stored.
     */
    public PropertiesManager(String moduleName)
    {
        m_module_name = moduleName + ".";
        //!! log.info( "Just created PropertiesManager for module " + moduleName );
    }

    /***
     *  Read properties for this application-type and any project-specific properties.
     *  If {@code ApplPropertiesFilename} exists, it provides defaults.
     *  Override values are stored (eventually) into {@codeProjectPropertiesFilename ).
     *
     * @param applFilename - Default setting-properties for this application's install file name.
     * @param projectFilename - Project-specific setting-properties file name.
     * @return true if found system-wide defaults, false if not found.
     */
    public Boolean readProperties(String applFilename, String projectFilename)
    {
        ApplPropertiesFilename = applFilename;
        ProjectPropertiesFilename = projectFilename;
        Boolean     good_read = true;

        Properties def_props = new Properties();
        s_from_disk = new Properties( def_props );
        s_props = new Properties( s_from_disk );

        //  1.  Read Application-wide properties (as the base).
        FileInputStream ins = null;
        try
        {
            ins = new FileInputStream(ApplPropertiesFilename);
            def_props.load(ins);
        }
        catch( Exception ex )
        {
            // skip it.
            good_read = false;
        }
        finally
        {
            if( ins != null )
                try { ins.close(); } catch( Exception e ) { }
        }

        //  2.  Read properties specifically for this project (overrides application-wide).
        try
        {
            ins = new FileInputStream(ProjectPropertiesFilename);
            s_from_disk.load(ins);
        }
        catch( Exception ex )
        {
            // skip it.
        }
        finally
        {
            try { ins.close(); } catch( Exception e ) { }
        }

        return good_read;
    }

    /***
     *  Write properties to the project-specific file location.
     *  They'll be available the next time application runs and will override default settings.
     *  Does not write the "temp" properties.
     *
     *  Must have already called {@code readProperties()} to set file names.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeProperties()
            throws FileNotFoundException, IOException
    {
        FileOutputStream  outs = new FileOutputStream(ProjectPropertiesFilename);
        writeProperties( outs );
    }

    /***
     *  Write properties to the given output stream.
     *  They're be available the next time application runs and will override default settings.
     *  Does not write the "temp" properties.
     *
     * @throws IOException
     */
    public void writeProperties( OutputStream outs )
            throws IOException
    {
        s_from_disk.store( outs, (ApplicationName == null) ? "(settings)" : ApplicationName );
    }

    /***
     *  Version of {@code writeProperties()} that does not thrown an exception.
     *  Also dumps both long-term and temporary properties.
     * @param outs
     */
    public void dumpAllProperties( OutputStream outs )
    {
        try {
            s_props.store( outs, (ApplicationName == null) ? "(all-settings)" : ApplicationName );
        } catch( Exception ex ) {
            //  nothing is possible except maybe logging error.  If 'outs' can't be
            //  written to, where can the "error writing" message be sent?
        }
    }

    // ----------------------------------------------------------------------------

    public void put(String key, String value)
    {
        String full_key = m_module_name + key;
        s_from_disk.setProperty( full_key, value );
    }

    public void putInt(String key, int ivalue)
    {
        String full_key = m_module_name + key;
        s_from_disk.setProperty( full_key, Integer.toString( ivalue ) );
    }

    /***
     *  Retrieve key for this module, or {@code null} if key not found
     * @param key property key , within module scope.
     * @return value, or {@code null} if key not found
     */
    public String get( String key )
    {
        String full_key = m_module_name + key;
        return (String) s_props.get( full_key );
    }

    public String get( String key, String defaultValue )
    {
        String full_key = m_module_name + key;
        return (String) s_props.getProperty( full_key, defaultValue );
    }

    public int getInt( String key, int defaultValue )
    {
        String  full_key = m_module_name + key;
        String  sval = (String) s_props.getProperty( full_key );
        int     ival = defaultValue;

        if( ! StringUtils.emptyOrNull( sval ) )
        {
            ival = Integer.parseInt( sval.trim() );
        }
        return ival;
    }

    // ----------------------------------------------------------------------------

    /***
     *   Add property that will NOT be written to disk.
     *   E.g. a property for just this application-run that was set on the command line.
     * @param key
     * @param value
     */
    public void tempPut( String key, String value )
    {
        String full_key = m_module_name + key;
        s_props.setProperty( full_key, value );
    }

    // ----------------------------------------------------------------------------

    public void clearManyList( String key )
    {

    }

    /***
     *  Some properties can have multiple instances, up to 9999.
     *  E.g. the last five open files will be stored as:
     * <ul> file.lastopen.1=sample.txt <br/>
     *      file.lastopen.2=example.txt <br/>
     *      file.lastopen.3=ouch.txt <br/>
     *      file.lastopen.4=dual.txt <br/>
     *      file.lastopen.5=likely.txt <br/>
     *  </ul>
     * Once a gap in the sequence is detected, the list has ended.
     * I.e. if {@code file.lastopen.3} were missing, then the list is considered
     * to end at "2" and not "5".
     *
     * @param key
     * @return Array of values, in numeric order.
     */
    public ArrayList<String> getManyList( String key )
    {
        ArrayList<String>  list = new ArrayList<String>();

        for( int j = 1 ; j <= 9999 ; ++j )
        {
            String  full_key = this.m_module_name + key + "." + j;
            String value = s_props.getProperty( full_key );
            if( null == value )
                break;      //  There are no more; we're at the list end.

            list.add( value );
        }

        return list;
    }

    /***
     *  Add a new item (to the head of the list) of a list-type property.
     *  If too many are being stored, then all excess are trimmed away.
     *  E.g. if last time {@code maxCount} was 9 but this time is 4,
     *  then the oldest 6 will be trimmed away with this latest
     *  {@code key} taking over first place.
     *
     * @param key a list-type property, within the module's scope.
     * @param value value to add.
     * @param maxCount Upper-bound of number of values to keep under this key.
     * @throws IndexOutOfBoundsException if {@code maxCount} &lt;= 0.
     */
    public void putManyListNext( String key, String value, int maxCount )
    {
        if( maxCount <= 0 )
            throw new IndexOutOfBoundsException("putManyListNext() maxCount <= 0");

        //  Trim list down to N-1 before insert.
        List<String>  list = getManyList( key );
        String  base_key_str = this.m_module_name + key + ".";

        while( list.size() >= maxCount )
        {
            //  Remove from properties Hashtable, too.
            String  full_key = base_key_str + (list.size());
            s_props.remove( full_key );

            //  Remove from list so won't be written back.
            list.remove( list.size() - 1 );
        }
        //  Insert caller's 'value' at head of list
        list.add( 0, value );

        //  Now update all the properties ( overwrites previous settings )
        for( int j = 0 ; j < list.size() ; ++j )
        {
            String  full_key = base_key_str + (j+1);
            s_props.setProperty( full_key, list.get( j ) );
        }
    }

    // ----------------------------------------------------------------------------

    private static Properties   s_from_disk;

    /** Place to store temp properties.  Uses {@code s_from_disk} as backing for persistent properties. */
    private static Properties   s_props;

    /** Each instance gets its own module name, used as prefix when storing properties. */
    private String      m_module_name;

    private static final Logger LOG = Logger.getLogger( PropertiesManager.class.getName() );
}
