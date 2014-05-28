/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio.cmri;

// import java.lang.Integer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.embeddedrailroad.eri.layoutio.LayoutIoModel;


/**
 *
 * @author brian
 * @param <java>
 */
public class CmriLayoutModelImpl<Integer> implements LayoutIoModel
{
    public CmriLayoutModelImpl()
    {
        m_inputs = new HashMap<>();
        m_blobs = new HashMap<>();
    }

    //---------------------  Object & Type Properties  --------------------

    @Override
    public String getIoSystemName() {
        return "C/MRI";
    }

    @Override
    public String getIoSystemManufacturer() {
        return "JLC Enterprises";
    }

    //--------------------------  DATA SETTORS  --------------------------

    @Override
    public void setSensedBinaryData( Comparable device, boolean[] new_bits )
    {
        try
        {
            Integer  dev = (Integer) device;
            m_inputs.put( dev, (boolean[]) new_bits.clone() );
        }
        catch( Throwable ex )
        {
            System.out.println( "ERROR in setSensedBinaryData(): " + ex.toString() );
        }
    }

    @Override
    public void setSensedBinaryData( Comparable device, HashMap individual_bits )
    {
        /***
         *  'individual_bits' is type HashMap< Integer, Boolean > for CMRI.
         *  The 'individual_bits' are copied one-at-a-time into the object.
         */
        try
        {
            Integer  dev = (Integer) device;

            //  1.  Determine highest bit number in individual_bits
            Set<java.lang.Integer>   keys = individual_bits.keySet();
            int  max = 0;

            for( java.lang.Integer itemp : keys )
            {
                if( max  < itemp.intValue() )
                    max = itemp.intValue();
            }
            max += 1;

            //  2.  We must have 'max' bits in order to store them all.
            //      Ensure HashMap.get(device) is big enough, resize if too small.
            boolean[]   dev_bits = m_inputs.get( device );
            if( dev_bits == null )
            {
                //  Nothing there, so make it right size.
                dev_bits = new boolean[ max ];
                m_inputs.put( dev, dev_bits );
            }
            else if( dev_bits.length < max )
            {
                //  More bits have arrived, extend size.
                dev_bits = Arrays.copyOf( dev_bits, max );
                m_inputs.put( dev, dev_bits );
            }

            //  3.  Copy of the changes, not changing if not mentioned in 'individual_bits'.
            for (Object entry : individual_bits.entrySet() )
            {
                Boolean  there = (Boolean) individual_bits.get( entry );
                dev_bits[ ((java.lang.Integer)entry).intValue() ] = there.booleanValue();
            }
        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in setSensedBinaryData(" + device.toString() + "): " + ex.toString() );
        }
    }

    @Override
    public void setSensedBinaryBlob( Comparable device, int subfunction, byte[] blob )
    {
        /**
         *  E.g. device "6.22.19" on subfunction "3" has bytes from an RFID reader,
         *  bytes "0x56 0x7F 0xA2 0x33 0xFF".  By setting that blob, the previous
         *  blob from the RFID reader is overwritten.
         *
         *  This great for mapping a CV value to a sub-function, so blob is an array
         *  of one 8-bit value.
         *
         *  The 'blob' is cloned before storing.
         */
        try
        {
            Integer  dev = (Integer) device;
            Integer  subf = (Integer) new java.lang.Integer(subfunction);

            //  1.  Retrieve all blobs for this device, i.e. function
            if( ! m_blobs.containsKey( dev ) )
            {
                m_blobs.put( dev, new  HashMap< Integer, byte[] >() );
            }
            HashMap< Integer, byte[] >  funct = m_blobs.get( dev );

            //  2.  Store the sub-function blob.
            funct.put( subf, (byte[]) blob.clone() );
        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in setSensedBinaryData(" + device.toString() + "): " + ex.toString() );
        }
    }

    //--------------------------  DATA GETTORS  --------------------------

    @Override
    public boolean[] getSensedDataAll( Comparable device )
    {
        /***
         *  Returned array is direct reference to what is stored, so please don't
         *  modify it.  However, caller can modify it, just be careful, OK?
         */
        Integer  dev = null;
        try
        {
            dev = (Integer) device;

            //  1.  Retrieve all blobs for this device, i.e. function
            if( ! m_inputs.containsKey( dev ) )
            {
                return new boolean[0];
            }

        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in getSensedDataAll(" + device.toString() + "): " + ex.toString() );
            return new boolean[0];
        }

        return m_inputs.get( dev );
    }

    @Override
    public boolean getSensedDataOne( Comparable device, int bit_number )
            throws ArrayIndexOutOfBoundsException
    {
        boolean[]   whole = getSensedDataAll( device );

        if( bit_number < 0 || bit_number >= whole.length )
        {
            throw new ArrayIndexOutOfBoundsException( "getSensedDataOne(" + device.toString() +"," + bit_number + ") out-of-range" );
        }

        return whole[ bit_number ];
    }

    @Override
    public byte[] getSensedBlob( Comparable device, int subfunction )
    {
        /***
         *  Return byte[] blob if known, else null if unknown.
         */
        Integer  dev = null;
        try
        {
            dev = (Integer) device;
            if( m_blobs.containsKey( device) )
            {
                Integer  subf = (Integer) new java.lang.Integer(subfunction);
                return m_blobs.get( dev ).get( subf );
            }
        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in getSensedDataAll(" + device.toString() + "): " + ex.toString() );
        }

        return null;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /***
     *  HashMap of boolean input bits, indexed by device address.
     *  {@code m_inputs.get()} returns the whole array for one device.
     */
    private HashMap< Integer, boolean[] >   m_inputs;

    private HashMap< Integer, HashMap< Integer, byte[] > >  m_blobs;
}
