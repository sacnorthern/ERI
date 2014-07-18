/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.HashMap;

/**
 *   Provide interface to a layout IO "provider".  Allows construction of IO-specific
 *   objects in its model, plus access to the IO-specific transport communication
 *   mechanism.
 *
 * @author brian
 */
public interface LayoutIoProvider
{
    /*** @return name of provider, same as IO model's name. */
    public String   getName();

    /*** @return list of transport channels available. */
    public HashMap<Integer, LayoutIoTransport> getTransportChannelList();

    /***
     *  Opens a numbered transport channel.
     *  If already opened / existing, then polling will stop.
     * @param channel number that IO model-specific.
     * @return
     */
    public boolean openChannel( Integer channel );

    /***
     *  Convert a string-form of a unit's address into native object-type.
     *
     * @param addr A string, i.e. from a GUI.
     * @return converted into an addressable unit
     * @throws IllegalArgumentException when {@code addr} is ill-formed.
     */
    public Object   convertUnitAddressString(String addr)
            throws IllegalArgumentException, NumberFormatException;

    /***
     *   Convert a single IO line address into native object-type.
     *
     * @param bit which single IO line referencing.
     * @return converted into an input or output line.
     * @throws IllegalArgumentException when {@code bit} is ill-formed.
     */
    public Object   convertIoBitAddressString(String bit)
            throws IllegalArgumentException, NumberFormatException;

    /***
     *   Create an address for a single bit, either input or output, or both.
     * @param addr Parameter to {@link convertUnitAddressString()}.
     * @param bit Parameter to {@link convertIoBitAddressString()}.
     * @param mode read, write or both.  Not all modes are implemented.
     * @return ???
     * @throws IllegalArgumentException Conversion failed.
     * @throws NumberFormatException {@code addr}, {@code bit_start}, or {@code bit_end} are
     *                              not valid or unimplemented place.
     */
    public Object   createAddressBit( String addr, String bit, String mode )
            throws IllegalArgumentException, NumberFormatException;

    /***
     *  Some devices can support a range of bits as a single I/O unit, e.g. an RFID reader
     *  or a turntable indexing mechanism.
     *
     * @param addr unit address parameter to {@link convertUnitAddressString()}.
     * @param bit_start first bit, parameter to {@link convertIoBitAddressString()}.
     * @param bit_end last bit, parameter to {@link convertIoBitAddressString()}.
     * @param mode read, write or both.  Not all modes are implemented.
     * @return ???
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
