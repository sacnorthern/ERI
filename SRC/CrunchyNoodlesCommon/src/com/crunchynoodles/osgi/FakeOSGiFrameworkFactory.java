/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2016 in USA by Brian Witt , bwitt@value.net
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

package com.crunchynoodles.osgi;

import com.crunchynoodles.util.FileUtils;
import com.crunchynoodles.util.StringUtils;
import java.io.File;
import java.util.Map;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.Constants;


/**
 *  Single method class-type to create appropriate bundle loading framework
 *  based on configuration properties map.
 *  After receiving the framework, you must {@code init Framework#init()} it.
 *
 * <p> Usage: <br>
 * <code>Framework fw = new FakeOSGiFrameworkFactory( propertiesMap );
 * fw.init();</code>
 *
 * @author brian
 */
public class FakeOSGiFrameworkFactory implements FrameworkFactory
{
    /*** Extra capabilities, fill in as needed. OK to change, but must be done before calling newFramework(). */
    public static String  FRAMEWORK_SYSTEM_EXTRA_CAPS = "";

    /*** Extra packages to add, fill in as needed. OK to change, but must be done before calling newFramework(). */
    public static String  FRAMEWORK_FRAMEWORK_EXTRA_PKGS = "";

    public final static String  CAPA_SEP = ", " ;

    @Override
    public Framework newFramework( Map<String, String> config_settings )
    {
        //  If there are any "GTK_*" env-vars, then change windowing system to GTK.

        //  "The configuration properties may contain any implementation specific properties.
        //   The properties in Table 4.1 must be supported by all conformant frameworks."
        //
        //  "osgi.core-6.0.0.pdf", section 4.2.2 , table 4.1 lists a ton of
        //  properties that must be filled in.  Go thru them one-by-one providing
        //  appropriate values.  Uses values from org.osgi.framework.Constants for
        //  "string" keys.

        if( ! config_settings.containsKey( Constants.FRAMEWORK_BOOTDELEGATION ) )
            config_settings.put( Constants.FRAMEWORK_BOOTDELEGATION, "false" );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_BSNVERSION ) )
            config_settings.put( Constants.FRAMEWORK_BSNVERSION, Constants.FRAMEWORK_BSNVERSION_SINGLE );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_BUNDLE_PARENT ) )
            config_settings.put( Constants.FRAMEWORK_BUNDLE_PARENT, Constants.FRAMEWORK_BUNDLE_PARENT_BOOT );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_EXECPERMISSION ) )
        {
            String  chmod = "";
            if( FileUtils.isOsKindOf( FileUtils.OS_LINUX ) )
                chmod = "chmod +rx ${abspath}" ;
            config_settings.put( Constants.FRAMEWORK_EXECPERMISSION, chmod );
        }

        /** DEPRECATED : FRAMEWORK_EXECUTIONENVIRONMENT **/

        if( ! config_settings.containsKey( Constants.FRAMEWORK_LANGUAGE ) )
            config_settings.put( Constants.FRAMEWORK_LANGUAGE, "en_us" );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_LIBRARY_EXTENSIONS ) )
        {
            // "A comma separated list of additional library file extensions
            //  that must be used when searching for native code."
            String  dlls = "dll";
            if( FileUtils.isOsKindOf( FileUtils.OS_LINUX ) )
                dlls = "a,so,dll";
            config_settings.put( Constants.FRAMEWORK_LIBRARY_EXTENSIONS, dlls );
        }

        if( ! config_settings.containsKey( Constants.FRAMEWORK_OS_NAME ) )
        {
            String  name = "unknown";
            //  Table 4.3 provides OSGi standard names for OS.
            switch( FileUtils.getOsBrand() )
            {
                case FileUtils.OS_LINUX :           name = "Linux";     break;
                case FileUtils.OS_MAC_X86 :         name = "MacOSX";    break;
                case FileUtils.OS_MAC_PPC :         name = "MacOS";     break;
                case FileUtils.OS_SOLARIS :         name = "Solaris";   break;
                case FileUtils.OS_WINDOWS_SINGLE_USER : name = "Windows98";  break;
                case FileUtils.OS_WINDOWS_MULTI :   name = "Win32";     break;
            }

            config_settings.put( Constants.FRAMEWORK_OS_NAME, name );
        }

        if( ! config_settings.containsKey( Constants.FRAMEWORK_OS_VERSION ) )
        {
            String  release_number = System.getProperty( "os.version" );
            config_settings.put( Constants.FRAMEWORK_OS_VERSION, release_number );
        }

        if( ! config_settings.containsKey( Constants.FRAMEWORK_PROCESSOR ) )
        {
            //  "Table 4.2 defines a list of processor names."
            String  proc = System.getProperty( "os.arch" );
            config_settings.put( Constants.FRAMEWORK_PROCESSOR, proc );

        }

        if( ! config_settings.containsKey( Constants.FRAMEWORK_SECURITY ) )
            config_settings.put( Constants.FRAMEWORK_SECURITY, Constants.FRAMEWORK_SECURITY_OSGI );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_BEGINNING_STARTLEVEL ) )
            config_settings.put( Constants.FRAMEWORK_BEGINNING_STARTLEVEL, "3" );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_STORAGE ) )
        {
            //  Home-dir is safeset as different running instances will not interfere with
            //  each other.
            String   work_folder = System.getProperty("user.home") + File.separator + "osgi" ;

            config_settings.put( Constants.FRAMEWORK_STORAGE, work_folder );
        }

        /** OPTIONAL VALUE : FRAMEWORK_STORAGE_CLEAN **/

        if( ! config_settings.containsKey( Constants.FRAMEWORK_STORAGE_CLEAN ) )
            config_settings.put( Constants.FRAMEWORK_STORAGE_CLEAN, "false" );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_SYSTEMCAPABILITIES ) )
            config_settings.put( Constants.FRAMEWORK_SYSTEMCAPABILITIES, "osgi.ee" );

        // org.osgi.framework.system.capabilities.extra :
        //  "Capabilities defined in this property are added to the
        //   org.osgi.framework.system.capabilities property. The purpose of the extra
        //   property is to be set by the deployer."

        if( ! config_settings.containsKey( Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA ) &&
            ! StringUtils.emptyOrNull( FRAMEWORK_SYSTEM_EXTRA_CAPS ) )
            config_settings.put( Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA, FRAMEWORK_SYSTEM_EXTRA_CAPS );

        if( ! config_settings.containsKey( Constants.FRAMEWORK_SYSTEMCAPABILITIES ) )
            config_settings.put( Constants.FRAMEWORK_SYSTEMCAPABILITIES, "osgi.ee" );

        /*** org.osgi.framework.system.packages : Nothing to do ***/

        if( ! config_settings.containsKey( Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA ) &&
            ! StringUtils.emptyOrNull( FRAMEWORK_FRAMEWORK_EXTRA_PKGS ) )
            config_settings.put( Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, FRAMEWORK_SYSTEM_EXTRA_CAPS );

        /*** org.osgi.framework.trust.repositories : Don't know what to do ***/

        // org.osgi.framework.windowsystem : "Windows", "gtk" or "x11"
        if( ! config_settings.containsKey( Constants.FRAMEWORK_WINDOWSYSTEM ) )
        {
            // "This can be used by the native code clause, Native Code Algorithm on page 75."
            String  win = "";
            switch( FileUtils.getOsBrand() )
            {
                case FileUtils.OS_LINUX :               win = "x11";        break;
                case FileUtils.OS_MAC_X86 :             win = "cocoa";      break;
                case FileUtils.OS_MAC_PPC :             win = "quartz";     break;
                case FileUtils.OS_SOLARIS :             win = "x11";        break;
                case FileUtils.OS_WINDOWS_SINGLE_USER : win = "windows";    break;
                case FileUtils.OS_WINDOWS_MULTI :       win = "windows";    break;
            }

            //  Search env-vars for something "GTK_*" to indicate GTK windowing.
            //  See https://developer.gnome.org/gtk3/stable/gtk-running.html
            Map<String, String>  envs = System.getenv();
            for( String key : envs.keySet() )
            {
                if( key.startsWith( "GTK_" ) || key.startsWith( "GTK3_" ) || key.startsWith( "XDG_DATA_" ) ) {
                    win = "gtk";
                    break;
                }
            }

            config_settings.put( Constants.FRAMEWORK_WINDOWSYSTEM, win );
        }

        // ------------------------------------------------------------

        //  Append extra stuff ...
        _concatProps( config_settings, Constants.FRAMEWORK_SYSTEMCAPABILITIES, Constants.FRAMEWORK_SYSTEMCAPABILITIES_EXTRA );

        _concatProps( config_settings, Constants.FRAMEWORK_SYSTEMPACKAGES, Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA );

        // ------------------------------------------------------------

        //  Clean out OSGi storage if called for.
        String  when = config_settings.get( Constants.FRAMEWORK_STORAGE_CLEAN );
        if( when.equalsIgnoreCase( Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT ) )
        {
            String  dir = config_settings.get( Constants.FRAMEWORK_STORAGE );
            if( ! StringUtils.emptyOrNull( dir ) )
                FileUtils.deleteDirectory( when );
        }

        // "A new, configured {@link Framework} instance. The framework
	//  instance must be in the {@link Bundle#INSTALLED} state [afterwards]."
        return new FakeOSGiFramework( config_settings );
    }

    /***
     *   Concatenate extra settings, if not empty, to a base string.
     *   Updates mapping in place.
     *
     * @param config_settings Mapping to update
     * @param capa_base key to appended to
     * @param capa_extra key of optional extras to append.
     */
    private void _concatProps( Map<String, String> config_settings,
                               final String  capa_base,
                               final String  capa_extra )
    {
        String  extra = config_settings.get( capa_extra );
        String  caps  = config_settings.get( capa_base );
        if( ! StringUtils.emptyOrNull( extra ) )
        {
        //  Append ...
            caps = caps + CAPA_SEP + extra;
            config_settings.put( capa_base, caps );
        }
    }

}
