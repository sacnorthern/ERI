/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio.cmri;

// import java.lang.Integer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.embeddedrailroad.eri.layoutio.LayoutIoModel;


/**
 *
 * @author brian
 * @param <java>
 */
public class CmriLayoutModelImpl<T> implements LayoutIoModel<Integer>
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
    public void setSensedBinaryData( Integer device, boolean[] new_bits )
    {
        /***
         *  'device' cannot be null.
         *  'new_bits' as null means remove info about 'device'.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        m_lock.writeLock().lock();
        try
        {
            Integer  dev = (Integer) device;
            if( new_bits != null )
            {
                m_inputs.put( dev, (boolean[]) new_bits.clone() );
            }
            else
            {
                m_inputs.remove( dev );
            }
        }
        catch( Throwable ex )
        {
            System.out.println( "ERROR in setSensedBinaryData(): " + ex.toString() );
            ex.printStackTrace( System.out );
        }
        finally
        {
            m_lock.writeLock().unlock();
        }
    }

    @Override
    public void setSensedBinaryData( Integer device, HashMap<Integer, Boolean> individual_bits )
    {
        /***
         *  'individual_bits' is type HashMap< Integer, Boolean > for CMRI.
         *  The 'individual_bits' are copied one-at-a-time into the object.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");
        if( individual_bits == null )
            return;

        m_lock.writeLock().lock();
        try
        {
            Integer  dev = (Integer) device;

            //  1.  Determine highest bit number in individual_bits
            Set<Integer>   keys = individual_bits.keySet();
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
            //      With a Set, bit numbers can be in any order!
            //      I had a lot of trouble getting the types to match up...  :(
            Iterator  everything = individual_bits.entrySet().iterator();

            while( everything.hasNext() )
            {
                Map.Entry<Integer, Boolean>  entry = (Map.Entry<Integer, Boolean>) everything.next();

                Boolean  there = (Boolean) entry.getValue();
                dev_bits[ entry.getKey() ] = there.booleanValue();
            }
        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in setSensedBinaryData(" + device.toString() + "): " + ex.toString() );
            ex.printStackTrace( System.out );
        }
        finally
        {
            m_lock.writeLock().unlock();
        }
    }

    @Override
    public void setSensedBinaryBlob( Integer device, int subfunction, byte[] blob )
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
         *  If null, then it is removed.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        m_lock.writeLock().lock();
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
            if( blob != null )
            {
                funct.put( subf, (byte[]) blob.clone() );
            }
            else
            {
                funct.remove( subf );
            }
        }
        catch( Throwable ex )
        {
            System.out.println("ERROR in setSensedBinaryData(" + device.toString() + "): " + ex.toString() );
            ex.printStackTrace( System.out );
        }
        finally
        {
            m_lock.writeLock().unlock();
        }
    }

    //--------------------------  DATA GETTORS  --------------------------

    @Override
    public boolean[] getSensedDataAll( Integer device )
    {
        /***
         *  Returned array is direct reference to what is stored, so please don't
         *  modify it.  However, caller can modify it, just be careful, OK?
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

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
            ex.printStackTrace( System.out );
            return new boolean[0];
        }

        return m_inputs.get( dev );
    }

    @Override
    public boolean getSensedDataOne( Integer device, int bit_number )
            throws ArrayIndexOutOfBoundsException
    {
        if( device == null )
            throw new NullPointerException("device cannot be null");

        boolean[]   whole = getSensedDataAll( device );

        if( bit_number < 0 || bit_number >= whole.length )
        {
            throw new ArrayIndexOutOfBoundsException( "getSensedDataOne(" + device.toString() +"," + bit_number + ") out-of-range" );
        }

        return whole[ bit_number ];
    }

    @Override
    public byte[] getSensedBlob( Integer device, int subfunction )
    {
        /***
         *  Return byte[] blob if known, else null if unknown.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        Integer  dev = null;

        m_lock.readLock().lock();
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
            ex.printStackTrace( System.out );
        }
        finally
        {
            m_lock.readLock().unlock();
        }

        return null;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /***
     *  Data access READER/WRITER lock.
     *  @see https://www.obsidianscheduler.com/blog/java-concurrency-part-2-reentrant-locks/
     */
    private ReadWriteLock   m_lock = new ReentrantReadWriteLock();

    /***
     *  HashMap of boolean input bits, indexed by device address.
     *  {@code m_inputs.get()} returns the whole array for one device.
     */
    private HashMap< Integer, boolean[] >   m_inputs;

    private HashMap< Integer, HashMap< Integer, byte[] > >  m_blobs;
}
