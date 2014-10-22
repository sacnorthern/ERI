/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Interface of all layout IO models; these hold state of inputs and allow setting of
 *  outputs in a form the underlying IO systems prefers.
 *  A model is owned and maintained by a single {@link LayoutIoTransport} object.
 *  Assumes there is an addressing scheme for devices, and that units have individual bits
 *  that can be sensed or controlled.
 *
 *  <p> Methods like {@link #setSensedBinaryData(java.lang.Comparable, boolean[]) }
 *  are used by the transport-object to populate the model.
 *  Parts of the array of bits can be read one bit at a time, or as a block.
 *
 *  <p> Units can also have structured data, the are referred to as "blobs" here,
 *  since the model itself imposes no meaning and structure upon them.
 *  Each unit's blob has a unique index (might not be consecutive),
 *  and writing a new blob to an existing index changes the blob's value atomically.
 *  The previous blob's value is gone.
 *  An example of a blob is the 64-bit RFID reader response.
 *
 *  <p> Generally speaking, a layout model is totally "transient".
 *  No values are saved from one program-run to another.
 *  For some things, e.g. turnouts with no feedback, there should be some
 *  long-term, non-volatile storage of values.
 *  That effort is for another day.....
 *
 * <p> See http://www.onjava.com/pub/a/onjava/2004/07/07/genericmvc.html
 *
 * @author brian
 * @param <TUnitAddr> Node address type, e.g.{@link Integer} or {@link java.net.InetAddress}.
 */
public interface LayoutIoModel< TUnitAddr extends Comparable<TUnitAddr> > {

    /***
     *  Get class-type of address {@code TUnitAddr} thing.
     * @return class-type of address {@code TUnitAddr} thing.
     */
    public Class   getUnitAddressType();

    //-------------------  UNIT INITIALIZATION AND SETUP  ---------------------

    /***
     *  Store the initialization messages for some unit in the bank.
     *  If {@link mesgs} is null, then the transport revive logic cannot determine
     *  if a unit is present or not.
     *
     * @param unit Address of unit.
     * @param mesgs array of strings, or null if nothing to send.
     */
    public void    setUnitInitializationStrings( TUnitAddr unit, ArrayList<byte[]> mesgs );

    /***
     *  Retrieve the initialization strings for some unit in the bank.
     *  For speed, the {@link ArrayList} is a new reference to same bytes as set.
     *
     * @param unit Address of unit.
     * @return array of messages to send, or null if nothing to send.
     */
    public ArrayList<byte[]>  getUnitInitializationStrings( TUnitAddr unit );

    /***
     *  Store message to send to unit for querying.
     * @param unit Address of unit.
     * @param query Query message, cannot be null or zero-length.
     */
    public void     setUnitQueryMessage( TUnitAddr unit, byte[] query );

    /***
     *  Retrieve the query message for a unit, which is same array as in set.
     *  Please do not modify the returned byte array.
     *
     * @param unit Address of unit.
     * @return array of bytes
     */
    public byte[]   getUnitQueryMessage( TUnitAddr unit );

    //------------------  STORING SENSED DATA FROM DEVICE  --------------------

    /***
     *  Received an update of all input bits from a device.
     *  If {@code newBits} is null, then it is removed.
     *
     * @param device address of device that gave data.
     * @param newBits array of them bits.
     */
    public void     setSensedBinaryData( TUnitAddr device, boolean[] newBits );

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
     *  If {@code blob} is null, then the sub-function is removed.
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
     * @param bitNumber the input sense to retrieve.
     * @return {@code true} if on, else {@code false} if off.
     * @exception ArrayIndexOutOfBoundsException if {@code bitNumber} out-of-bounds.
     */
    public boolean      getSensedDataOne( TUnitAddr device, int bitNumber )
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

}
