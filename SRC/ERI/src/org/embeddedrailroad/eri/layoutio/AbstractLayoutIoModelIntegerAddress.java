/***  Java-ERI    Java-based Embedded Railroad Interfacing.
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
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.layoutio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;


/***
 *  Abstract implementation for {@link LayoutIoModel<Integer>}.
 *
 * <p> See http://www.onjava.com/pub/a/onjava/2004/07/07/genericmvc.html
 *
 * @author brian
 */
public class AbstractLayoutIoModelIntegerAddress implements LayoutIoModel<Integer>
{
    public AbstractLayoutIoModelIntegerAddress()
    {
        m_inputs = new HashMap<>();
        m_blobs = new HashMap<>();
        m_init_msgs = new HashMap<>();
        m_query_msgs = new HashMap<>();
    }

    //---------------------  Object & Type Properties  --------------------

    @Override
    public Class   getUnitAddressType()
    {
        return Integer.class;
    }

    //-------------------  UNIT INITIALIZATION AND SETUP  ---------------------

    @Override
    public void    setUnitInitializationStrings( Integer unit, ArrayList<byte[]> mesgs )
    {
        m_init_msgs.put( unit, new ArrayList<> ( mesgs ) );
    }

    @Override
    public ArrayList<byte[]>  getUnitInitializationStrings( Integer unit )
    {
        return m_init_msgs.get(  unit );
    }

    @Override
    public void     setUnitQueryMessage( Integer unit, byte[] query )
    {
        m_query_msgs.put( unit, query );
    }

    @Override
    public byte[]   getUnitQueryMessage( Integer unit )
    {
        return m_query_msgs.get( unit );
    }

    //--------------------------  DATA SETTORS  --------------------------

    @Override
    public void setSensedBinaryData( Integer device, boolean[] newBits )
    {
        /***
         *  'device' cannot be null.
         *  'newBits' as null means remove info about 'device'.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        m_lock.writeLock().lock();
        try
        {
            if( newBits != null )
            {
                m_inputs.put( device, (boolean[]) newBits.clone() );
            }
            else
            {
                m_inputs.remove( device );
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

            //  1.  Determine highest bit number in individual_bits
            Set<Integer>   keys = individual_bits.keySet();
            int  max = 0;

            for( Integer itemp : keys )
            {
                if( max  < itemp )
                    max = itemp;
            }
            max += 1;

            //  2.  We must have 'max' bits in order to store them all.
            //      Ensure HashMap.get(device) is big enough, resize if too small.
            boolean[]   dev_bits = m_inputs.get( device );
            if( dev_bits == null )
            {
                //  Nothing there, so make it right size.
                dev_bits = new boolean[ max ];
                m_inputs.put( device, dev_bits );
            }
            else if( dev_bits.length < max )
            {
                //  More bits have arrived, extend size.
                dev_bits = Arrays.copyOf( dev_bits, max );
                m_inputs.put( device, dev_bits );
            }

            //  3.  Copy of the changes, not changing if not mentioned in 'individual_bits'.
            //      With a Set, bit numbers can be in any order!
            //      I had a lot of trouble getting the types to match up...  :(
            Iterator< Map.Entry<Integer, Boolean> >  everything = individual_bits.entrySet().iterator();

            while( everything.hasNext() )
            {
                Map.Entry<Integer, Boolean>  entry = everything.next();

                dev_bits[ entry.getKey() ] = entry.getValue();
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
         *  E.g. device "6.22.19" has sub-function "3" has bytes from an RFID reader,
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
            Integer  subf = subfunction;

            //  1.  Retrieve all blobs for this device, i.e. function
            if( ! m_blobs.containsKey( device ) )
            {
                m_blobs.put( device, new  HashMap< Integer, byte[] >() );
            }
            HashMap< Integer, byte[] >  funct = m_blobs.get( device );

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
            throws NullPointerException, UnknownLayoutUnitException
    {
        /***
         *  Returned array is direct reference to what is stored, so please don't
         *  modify it.  However, caller can modify it, just be careful, OK?
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        try
        {
            //  If no matching device is found, then toss cookies.
            if( ! m_inputs.containsKey( device ) )
            {
                throw new UnknownLayoutUnitException("device not found");
            }

        }
        catch( UnknownLayoutUnitException ex )
        {
            System.out.println("ERROR in getSensedDataAll(" + device.toString() + "): " + ex.toString() );
            ex.printStackTrace( System.out );
            return new boolean[0];
        }

        return m_inputs.get( device );
    }

    @Override
    public boolean getSensedDataOne( Integer device, int bit_number )
            throws ArrayIndexOutOfBoundsException, UnknownLayoutUnitException, NullPointerException
    {
        boolean[]   whole = getSensedDataAll( device );

        if( bit_number < 0 || bit_number >= whole.length )
        {
            throw new ArrayIndexOutOfBoundsException( "getSensedDataOne(" + device.toString() +"," + bit_number + ") out-of-range" );
        }

        return whole[ bit_number ] ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public byte[] getSensedBlob( Integer device, int subfunction )
    {
        /***
         *  Return byte[] blob if known, else null if unknown.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        m_lock.readLock().lock();
        try
        {
            if( m_blobs.containsKey( device) )
            {
                Integer  subf = subfunction;
                return m_blobs.get( device ).get( subf );
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

        //  An unknown device returns null instead of an exception.
        return null;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /***
     *  Data access READER/WRITER lock.
     *  @see https://www.obsidianscheduler.com/blog/java-concurrency-part-2-reentrant-locks/
     */
    transient private final ReadWriteLock   m_lock = new ReentrantReadWriteLock();

    /*** Store unit initialization strings, indexed by unit-address. */
    transient private HashMap< Integer, ArrayList<byte[]> >    m_init_msgs;

    transient private HashMap< Integer, byte[] >    m_query_msgs;

    /***
     *  HashMap of boolean input bits, indexed by device address.
     *  {@code m_inputs.get()} returns the whole array for one device.
     */
    transient private HashMap< Integer, boolean[] >   m_inputs;

    /***
     *  Recorded data-blobs from different units: primary index = unit, secondary index = device therein.
     *  Data blobs are as big as given to use, and can vary in size.
     */
    transient private HashMap< Integer, HashMap< Integer, byte[] > >  m_blobs;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( AbstractLayoutIoModelIntegerAddress.class.getName() );

}
