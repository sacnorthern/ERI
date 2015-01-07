/***  Java Commons and Niceties Library from CrunchyNoodles.com
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
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author brian
 */
public class UserDirectories {

    /*** Name of company producing the application.  Must be compatible with file naming. */
    public static String    CompanyName;

    /*** Application name.  Must be compatible with file naming. */
    public static String    ApplicationName;

    /*** File extension ( with dot ) of Java properties files. */
    public final static String  SETTINGS_FILE_EXTENSION = ".properties";

    // ----------------------------------------------------------------------------

    /***
     * Gather folder names.
     * Since class is a singleton, the shell-env and the properties are inspected
     * very early, like before main() has had a chance to run.
     *
     * <p> TODO: Adjust for Java Web Server and JNLP.
     */
    private UserDirectories()
    {
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
            //  Create a hidden folder in the home directory.
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

    /***
     *  Return singleton instance.
     * @return instance reference.
     */
    public static UserDirectories getInstance() {
        return UserDirectoriesHolder.INSTANCE;
    }

    private static class UserDirectoriesHolder {

        private static final UserDirectories INSTANCE = new UserDirectories();
    }


    // ----------------------------------------------------------------------------

    /**
     *  Folder of where to store the user's settings, e.g "/users/bwitt/.Com.CrunchyNoodles/ArduWriter/".
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
     *  File name is valid for all projects of this user.
     *
     * @return string concatenation of user-centric application-data-folder, application name,
     *         along with the appropriate file extension.
     */
    public String getUserApplSettingsPropertiesFilename()
    {
        return _appl_data_folder + ApplicationName + SETTINGS_FILE_EXTENSION;
    }

    // ----------------------------------------------------------------------------

    /**
     *  Read and store the application's properties in this file for this project.
     *  File name is valid for this particular project..
     *
     * @return string concatenation of project-centric application-data-folder, application name,
     *         along with the appropriate file extension.
     */
    public String getProjectApplSettingsFolder()
    {
        return _project_data_folder + ApplicationName + SETTINGS_FILE_EXTENSION;
    }

    // ----------------------------------------------------------------------------

    private String _appl_data_folder;
    private String _project_data_folder;
}
