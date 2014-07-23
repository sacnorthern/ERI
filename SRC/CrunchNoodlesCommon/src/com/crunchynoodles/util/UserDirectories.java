/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author brian
 */
public class UserDirectories {

    public static String    CompanyName;
    public static String    ApplicationName;

    public final static String  SETTINGS_FILE_EXTENSION = ".properties";

    // ----------------------------------------------------------------------------

    /***
     * Gather folder names.
     * Since class is a singleton, the shell-env and the properties are inspected
     * very early, like before main() has had a chance to run.
     *
     * <p> TODO: Adjust for Java Web Server and JNLP.
     */
    private UserDirectories() {
        Properties  p = System.getProperties();
        Map<String,String>  env = System.getenv();

        /***  User's Application profile folder ***/
        if( env.containsKey("windir") && env.containsKey("APPDATA") )
        {
            //  IT IS WINDOWS
            _appl_data_folder = ((String) env.get("APPDATA")) + File.separator + CompanyName + File.separator; // + ApplicationName + File.separator;
        }
        else if( env.containsKey("HOME") )
        {
            //  IT IS UNIX/LINUX/SOLARIS
            _appl_data_folder = ((String) env.get("HOME")) + File.separator + "." + CompanyName + File.separator; // + ApplicationName + File.separator;
        }
        else
        {
            //  Always have a default fallback: the current running directory.
            _appl_data_folder = p.getProperty("user.dir") + File.separator + ApplicationName + "-Settings" + File.separator;
        }

        /***  User's Application profile folder ***/
        _project_data_folder = p.getProperty("user.dir") + File.separator;
    }

    public static UserDirectories getInstance() {
        return UserDirectoriesHolder.INSTANCE;
    }

    private static class UserDirectoriesHolder {

        private static final UserDirectories INSTANCE = new UserDirectories();
    }


    // ----------------------------------------------------------------------------

    /**
     *  Folder of where to store the user's settings, e.g "/users/bwitt/.CrunchyNoodles/ArduWriter/".
     *  In the worst case, this will be the current directory.
     *
     * @return folder name with trailing {@code File.separator}.
     */
    public String getUserApplSettingsFolder()
    {
        return _appl_data_folder;
    }

    /**
     *  Read and store the application's properties in this file for this user.
     * @return
     */
    public String getUserApplSettingsPropertiesFilename()
    {
        return _appl_data_folder + ApplicationName + SETTINGS_FILE_EXTENSION;
    }

    // ----------------------------------------------------------------------------

    public String getProjectApplSettingsFolder()
    {
        return _project_data_folder + ApplicationName + SETTINGS_FILE_EXTENSION;
    }

    // ----------------------------------------------------------------------------

    private String _appl_data_folder;
    private String _project_data_folder;
}
