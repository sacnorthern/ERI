/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.util;

import java.io.*;
import java.lang.String;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//  These are for the directory search method:
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;
import java.util.*;


/**
 *  Utilities for files.
 * @author brian
 */
public class FileUtils {

    /***
     *  Reads whole file in and returns it as a string, even if file is binary.
     *
     * @param filename filename
     * @param charSet "UTF-8"
     * @return file as whole string, with line endings in tack.
     * @throws FileNotFoundException
     * @throws IOException
     * @see http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
     */
    public static String readWholeFile( String filename, String charSet )
            throws FileNotFoundException, IOException
    {
        FileInputStream   ins = new FileInputStream(filename);
        String  contents = null;
        try {
            contents = readWholeFile( ins, Charset.forName(charSet) );
        }
        finally {
            try { ins.close(); } catch( Exception ex ) { }
        }

        return contents;
    }

    /***
     *  Converts a {@code FileInputStream} into a big string.
     *  Handles various input-stream character sets, e.g. Latin-3 or UTF-8.
     *
     * @param ins stream to read
     * @param cs character set for single-byte to Unicode conversion.
     * @return A big string
     * @throws IOException
     */
    public static String readWholeFile( FileInputStream ins, Charset cs )
            throws IOException
    {
        Reader  reader = new BufferedReader( new InputStreamReader(ins, cs) );
        StringBuilder   sb = new StringBuilder(9999);
        char[]      buffer = new char[ 8100 ];
        int         cnt;

        while( (cnt = reader.read(buffer, 0, buffer.length)) > 0 )
        {
            sb.append( buffer, 0, cnt );
        }

        return sb.toString();
    }

    // ----------------------------------------------------------------------------

    /***
     *  Figures out the way to invoke the Arduino build environment.
     *
     * @param foundCmd Filled in with command [0] and any args in [1] to [N-1].
     * @return true when found, false if too obscure.
     */
    public static Boolean getArduinoRunCommand( List<String> foundCmd )
    {
        String   ARDUINO_APPL_NAME = "arduino";

        int[]   returnValue = new int[1];
        List<String>  check_output = null;
        String[]    check_cmd;
        List<String>  runit = null;
        String        runcmd = null;

        _determineOsBrand();

        try {

            switch( s_osBrand )
            {
                case OS_WINDOWS_MULTI :
                    check_cmd = new String[] { "reg", "QUERY", "HKCU\\Software\\Classes\\Applications\\" + ARDUINO_APPL_NAME + ".exe\\shell\\open\\command" };
                    check_output = runCommandGetOutput( null, check_cmd, returnValue );
                    if( returnValue[0] != 0 )
                        break;

                    // should get four lines, line #3: {{    (Default)    REG_SZ    "C:\pkg\Arduino\arduino-1.5.2-windows\arduino-1.5.2\arduino.exe" "%1"}}
                    //  Trim away all before double-quote.
                    runcmd = check_output.get( 2 );
                    int  reg_sz = runcmd.indexOf( "REG_SZ" );
                    if( reg_sz > 0 )
                    {
                        runcmd = runcmd.substring( reg_sz + 6 ).trim();
                        //  String should now be {{"C:\pkg\Arduino\arduino-1.5.2-windows\arduino-1.5.2\arduino.exe" "%1"}}
                        runit = StringUtils.tokenizeQuotedStrings( runcmd );
                    }

                    break;

                case OS_LINUX :
                case OS_MAC_X86 :
                case OS_SOLARIS :
                    check_cmd = new String[] { "which", ARDUINO_APPL_NAME };
                    check_output = runCommandGetOutput( null, check_cmd, returnValue );
                    if( returnValue[0] != 0 )
                        break;

                    //  should get one line, line #1 {{no arduino in /bin /sbin /usr/bin" ;
                    //  or on success, line #1 {{/usr/bin/arduino}}
                    runcmd = check_output.get(0);
                    if( runcmd == null || runcmd.startsWith( "no " + ARDUINO_APPL_NAME ) )
                    {
                        break;
                    }

                    runit.add( runcmd );
                    runit.add( "%1" );

                default :
                    return false;
            }
        }
        catch( Exception ex )
        {
            System.out.println( "getArduinoRunCommand() failed: " + ex.getMessage() );
        }

        if( runit == null )
        {
            return false;
        }

        //  Make it just what we have.  Must use addAll() cuz no pass-by-ref in Java.
        foundCmd.clear();
        foundCmd.addAll( runit );

        return true;
    }

    // ----------------------------------------------------------------------------

    /***
     *  Returns the list of serial comm ports, as appropriate for the OS platform.
     *  E.g. in MS-Windows, the list is { "COM1", "COM4" }.
     *  For MacOS, the list  is { "/dev/tty.Bluetooth-Modem", "/dev/tty.Bluetooth-PDA-Sync" }.
     *
     * @return List of strings to use for serial-comm ports, or null if errors.
     * @throws {@code FileNotFoundException} when can't determine any ports to return.
     * @throws {@code IOException} when trouble executing command to find serial-comm ports.
     *
     * @see https://www.microsoft.com/resources/documentation/windows/xp/all/proddocs/en-us/ts_cmd_changeport.mspx?mfr=true
     * @see http://pbxbook.com/other/mac-tty.html
     */
    public List<String> GetCommPortList()
            throws FileNotFoundException, IOException
    {
        List<String>  comm_ports = new ArrayList<String>();
        int[]   retval = new int[1];
        List<String>  outs;

        if( FileUtils.isOsKindOf( OS_WINDOWS_MULTI ) )
        {
            try {
                outs = runCommandGetOutput( null, new String[] { "change", "port", "/QUERY" }, retval );
                // for some reason, ERRORLEVEL is 1 after this program runs.
            } catch ( InterruptedException ex )
            {
                //  user hit control-C, just keep going...
                outs = null;
            }
            if( outs == null /* || retval[0] != 0 */ )
            {
                throw new FileNotFoundException("change port /QUERY");
            }

            /****
             * Output looks like below.  Grab only the COMn from lines.
             *      AUX = \DosDevices\COM1
             *      COM1 = \Device\Serial0
             *      COM4 = \Device\VCP0
             *      FTDIBUS#VID_0403+PID_6001+A7006R5MA#0000#{4d36e978-e325-11ce-bfc1-08002be10318} = \Device\00000075
             *      FTDIBUS#VID_0403+PID_6001+A7006R5MA#0000#{86e0d1e0-8089-11d0-9ce4-08003e301f73} = \Device\00000075
             */
            for( String line : outs )
            {
                if( line.startsWith( "COM" ) )
                {
                    int  e = line.indexOf( " = " );
                    if( e > 3 )
                    {
                        comm_ports.add( line.substring( 0, e) );
                    }
                }
            }
        }
        else
        if( FileUtils.isOsKindOf( OS_MAC_X86 ) )
        {
            MyFilesFinder  my_finder = new MyFilesFinder( "/dev", "tty.*" );
            try {
                my_finder.doIt();
                outs = my_finder.FileList;
            }
            catch( IOException ex )
            {
                // oh well, no devices found....
                outs = null;
            }

            comm_ports = outs;
            outs = null;
        }

        return comm_ports;
    }

    /***
     *  Find-files filter class.
     *  Client provides folder and file-glob pattern.
     *  Matches are available in {@code FileList}. <p>
     *
     *  <b>WARNING</b>: {@code PathMatcher} is Java 1.7 !!
     *
     * @see http://docs.oracle.com/javase/tutorial/essential/io/find.html
     */
    class MyFilesFinder extends SimpleFileVisitor<Path>
    {
        public List<String>    FileList;

        PathMatcher     m_matcher;
        Path            m_starting_dir;

        public MyFilesFinder( String folderName, String globPattern )
        {
            FileList = new ArrayList<String>();
            m_starting_dir = Paths.get( folderName );
            m_matcher = FileSystems.getDefault().getPathMatcher( "glob:" + globPattern );
        }

        public void doIt()
                throws IOException
        {
            Files.walkFileTree( m_starting_dir, this );
        }

        @Override
        public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs )
                throws IOException {
            // Looking just for files, so no need to check if matching.
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
                throws IOException {
            Path name = file.getFileName();
            if (name != null && m_matcher.matches(name)) {
                FileList.add( file.getFileName().toString() );
                System.out.println(file);
            }

            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed( Path file, IOException exc )
                throws IOException {
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( Path dir, IOException exc )
                throws IOException {
            return CONTINUE;
        }

    }

    // ----------------------------------------------------------------------------

    public static int getOsBrand()
    {
        if( s_osBrand < 0 )
        {
            _determineOsBrand();
        }

        return s_osBrand;
    }

    /***
     *  See if host OS matches the caller's kind-of interest.
     *  It is a braod-stroke, fuzzy match.
     *  E.g. {@code OS_MAC_PPC} and {@code OS_MAC_X86} are equivalent.
     *
     * @param brand Desired brand to check for.
     * @return {@code true} on OS brand kind-of matches .
     */
    public static boolean isOsKindOf( int brand )
    {
        int  os = getOsBrand();
        switch( brand )
        {
            case OS_WINDOWS_SINGLE_USER :
            case OS_WINDOWS_MULTI :
                if( os == OS_WINDOWS_SINGLE_USER || os == OS_WINDOWS_MULTI )
                    return true;
                break;

            case OS_MAC_PPC :
            case OS_MAC_X86 :
                if( os == OS_MAC_PPC || os == OS_MAC_X86 )
                    return true;
                break;

            case OS_LINUX :
            case OS_SOLARIS :
                //  These have only a single "OS Brand entry", so test for exact match.
                if( os == brand )
                    return true;
                break;

        }
        //  Unsure, or don't know the OS type....
        return false;
    }


    public static final int OS_UNKNOWN = -1;
    public static final int OS_WINDOWS_SINGLE_USER = 1;
    public static final int OS_WINDOWS_MULTI = 2;
    public static final int OS_MAC_PPC = 3;
    public static final int OS_MAC_X86 = 4;
    public static final int OS_LINUX = 5;       // bsd-like unix.
    public static final int OS_SOLARIS = 6;     // SVR4-like unix.

    private static int   s_osBrand = -1;

    private static void _determineOsBrand()
    {
        while( s_osBrand == OS_UNKNOWN )
        {
            //  Go feel around to determine OS brand.
            if( new File("C:\\Windows\\System32").isDirectory() && new File("C:\\Windows\\Fonts\\arial.ttf").isFile() )
            {
                s_osBrand = OS_WINDOWS_MULTI;
                break;
            }

            if( new File("/usr/bin").isDirectory() && new File("/etc").isDirectory() )
            {
                //  this could also by Cygwin under MS-Windows....

                int[]  retval = new int[1];
                try {
                    List<String>  out = runCommandGetOutput( null, new String[] { "uname", "-a" }, retval );
                    if( retval[0] != 0 )
                        break;
                    String[] words = out.get( 0 ).split( " " );
                    if( words[3].equalsIgnoreCase( "Darwin" ) )
                    {
                        s_osBrand = OS_MAC_X86;
                        break;
                    }
                    if( words[3].equalsIgnoreCase( "Linux" ) )
                    {
                        s_osBrand = OS_LINUX;
                        break;
                    }
                } catch( Exception ex ) {
                    // do nothing, will default to OS_LINUX below.
                }
            }

            //  oh well, let's just go with Linux as fallback....
            s_osBrand = OS_LINUX;
            break;
        }
    }


    // ----------------------------------------------------------------------------

    /***
     *  Execute process and collect all output.
     *  If exec failure, then returns {@code cmdReturnValue[0]} as -1 and {@code null}
     *  instead of string-array.
     *
     * @param startFolder Initial working directory, or null for user's home-dir.
     * @param args
     * @param cmdReturnValue - int[0] is return value of process
     * @return captured output as string on successful exec, else null if troubles.
     * @see http://stackoverflow.com/questions/1410741/want-to-invoke-a-linux-shell-command-from-java
     */
    public static List<String> runCommandGetOutput( String startFolder, String[] args, int[] cmdReturnValue )
            throws IOException, InterruptedException
    {
        List<String> commands = new ArrayList<String>();
        commands.addAll( Arrays.asList( args ) );

        cmdReturnValue[0] = -1;
        if( startFolder == null )
        {
            startFolder = System.getProperty( "user.home" );
        }

        //  Run program, gathering all its output.
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(startFolder));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        List<String>  out = new ArrayList<String>( 2000 );

        // Use the input stream connected to the normal and error output of the subprocess.
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        //Read output
        while ((line = br.readLine()) != null)
        {
            out.add( line );
        }

        cmdReturnValue[0] = process.waitFor();

        return out;
    }
}