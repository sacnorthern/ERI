
package org.embeddedrailroad.eri.layoutio;

import java.util.HashMap;
import org.osgi.annotation.versioning.Version;

/**
 *   Provide interface to a layout IO "provider".  Allows construction of IO-specific
 *   objects in its model, plus access to the IO-specific transport communication
 *   mechanism.
 *   There is one IO Provider per protocol, it is considered a singleton.
 *   However, because Java interfaces don't cover static class methods, this is hard to enforce.
 *
 * @author brian
 */
public interface LayoutIoProtocolProvider
{

    //--------------------------  DESIGNER  ---------------------------

    /***
     *  Return the name of this protocol provider that could include spaces and punctuation.
     *  E.g. "C/MRI" (punctuation is OK).
     *  @return name of provider, same as IO model's name.
     */
    public String   getProtocolName();

    /***
     *  Return a version string for this bundle, where "build" is an optional keyword.
     *  The "public version" is all chars before the first space.
     *  Do not put double-quotes into this string.
     *
     * @return String, e.g. "1.0.0 build 34838"
     */
    public String   getVersionString();

    /***
     *  Return producer / manufacturer of this IO system, e.g. "JLC Enterprises" (spaces likely).
     *  Is not necessarily the software maker.
     * @return string with likely spaces in it.
     */
    public String   getSystemManufacturer();

    /***
     *  Return a verbose and long string describing this protocol.
     *  If string starts with "&lt;html&gt;" then string is HTML formatted.
     *
     * @return A long string, ala short paragraph.
     */
    public String   getLongDescription();

    /***
     *  Return the registered IoTransports indexed by their channel number.
     * @return list of transport channels available.
     */
    public HashMap<Integer, LayoutIoTransport>  getTransportChannelList();

    /***
     *  Creates a numbered transport channel using a physical media and protocol.
     *  If already opened / existing, then polling will stop.
     *
     * @param physical Kind of physical hookup, e.g. "serial", "tcp", or "udp".
     * @param channel number that IO model-specific.
     * @return
     */
    public LayoutIoTransport  makeChannel( String physical, Integer channel );

    //--------------------  Addressing Conversion  --------------------

    /***
     *  Convert a string-form of a unit's address into native object-type.
     *  Each "layout IO protocol" has its own addressing scheme.
     *  E.g. CMRI address is an integer, so passing "12" returns a {@code new Integer(12)} object.
     *
     * @param addr A string, i.e. from a GUI.
     * @return converted into an addressable unit
     * @throws IllegalArgumentException when {@code addr} is ill-formed.
     */
    public Object   convertUnitAddressString(String addr)
            throws IllegalArgumentException, NumberFormatException;

    /***
     *   Convert a single IO line address into native object-type.
     *  Each "layout IO protocol" has its own addressing scheme.
     *
     * @param bit which single IO line referencing.
     * @return converted into an input or output line.
     * @throws IllegalArgumentException when {@code bit} is ill-formed.
     */
    public Object   convertIoBitAddressString(String bit)
            throws IllegalArgumentException, NumberFormatException;

    /***
     *   Create an address for a single bit, either input or output, or both.
     *  Each "layout IO protocol" has its own addressing scheme.
     *
     * @param addr Parameter to {@link #convertUnitAddressString()}.
     * @param bit Parameter to {@link #convertIoBitAddressString()}.
     * @param mode read, write or both.  Not all modes are implemented.
     * @return ???
     *
     * @throws IllegalArgumentException Conversion failed.
     * @throws NumberFormatException {@code addr}, {@code bit_start}, or {@code bit_end} are
     *                              not valid or unimplemented place.
     */
    public Object   createAddressBit( String addr, String bit, String mode )
            throws IllegalArgumentException, NumberFormatException;

    /***
     *  Some devices can support a range of bits as a single I/O unit, e.g. an RFID reader
     *  or a turntable indexing mechanism.
     *  Each "layout IO protocol" has its own addressing scheme.
     *
     * @param addr unit address parameter to {@link #convertUnitAddressString()}.
     * @param bit_start first bit, parameter to {@link #convertIoBitAddressString()}.
     * @param bit_end last bit, parameter to {@link #convertIoBitAddressString()}.
     * @param mode read, write or both.  Not all modes are implemented.
     * @return ???
     *
     * @throws IllegalArgumentException Conversion failed.
     * @throws NumberFormatException {@code addr}, {@code bit_start}, or {@code bit_end} are
     *                              not valid or unimplemented place.
     */
    public Object   createAddressBitRange( String addr, String bit_start, String bit_end, String mode )
            throws IllegalArgumentException, NumberFormatException;

    public final static String    MODE_READ = "read";
    public final static String    MODE_WRITE = "write";
    public final static String    MODE_READ_WITH_PULLUP = "read_up";
    public final static String    MODE_READ_WITH_INVERT = "read_invert";
    public final static String    MODE_READ_WITH_PULLUP_INVERT = "read_up_invert";

}
