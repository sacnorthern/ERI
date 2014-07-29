/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.HashMap;

/**
 *  Interface of all layout IO models; these hold state of inputs and allow setting of
 *  outputs in a form the underlying IO systems prefers.
 *  Assumes there is an addressing scheme for devices, and that devices have individual bits
 *  that can be sensed or controlled.
 *
 * @author brian
 */
public interface LayoutIoModel< TUnitAddr extends Comparable<TUnitAddr> > {

    /***
     *  Get class-type of address {@code TUnitAddr} thing.
     * @return class-type of address {@code TUnitAddr} thing.
     */
    public Class   getUnitAddressType();

    //------------------  STORING SENSED DATA FROM DEVICE  --------------------

    /***
     *  Received an update of all input bits from a device.
     *  If {@code new_bits} is null, then it is removed.
     *
     * @param device address of device that gave data.
     * @param new_bits array of them bits.
     */
    public void     setSensedBinaryData( TUnitAddr device, boolean[] new_bits );

    /***
     *  Received an updated of some input bits from a device.
     *  If {@code individual_bits} is null, then nothing changes.
     *
     * @param device address of device that gave data.
     * @param individual_bits map of which bit to what value.
     */
    public void     setSensedBinaryData( TUnitAddr device, HashMap<Integer, Boolean> individual_bits );

    /***
     *  A complex functional-unit on the device reported back a bunch of bytes.
     *  This could be an RFID reader.
     *  If {@code blob} is null, then the subfunction is removed.
     *
     * @param device address of device that gave data
     * @param subfunction functional unit on the device
     * @param blob bunch of bytes to store, or null.
     */
    public void     setSensedBinaryBlob( TUnitAddr device, int subfunction, byte[] blob );

    //------------------  READING SENSED DATA FROM DEVICE  --------------------

    /***
     *  Return all sensed data from a device.
     *  The return value is accumulated over all bulk and individual senses.
     *
     * @param device address of device
     * @return array of sensed data, indexed by position.
     */
    public boolean[]    getSensedDataAll( TUnitAddr device )
            throws UnknownLayoutUnitException, NullPointerException;

    /***
     *  Return the value of just one sensor.
     *
     * @param device address of device
     * @param bit_number the input sense to retrieve.
     * @return {@code true} if one, else {@code false} if off.
     * @exception ArrayIndexOutOfBoundsException if {@code bit_number} out-of-bounds.
     */
    public boolean      getSensedDataOne( TUnitAddr device, int bit_number )
            throws UnknownLayoutUnitException, NullPointerException, ArrayIndexOutOfBoundsException;

    /***
     *  Return a blob of data as sensed from a device.
     *
     * @param device address of device
     * @param subfunction functional unit on the device
     * @return Array of data, {@code null} if none recorded.
     */
    public byte[]       getSensedBlob( TUnitAddr device, int subfunction )
            throws UnknownLayoutUnitException, NullPointerException, ArrayIndexOutOfBoundsException;

    //--------------------------  DESIGNER  ---------------------------

    /***
     *  Return the name of this IO system, e.g. "C/MRI" (punctuation is OK).
     * @return string of IO system.
     */
    public String   getIoSystemName();

    /***
     *  Return producer / manufacturer of this IO system, e.g. "JLC Enterprises" (spaces likely).
     * @return string with likely spaces in it.
     */
    public String   getIoSystemManufacturer();
}
