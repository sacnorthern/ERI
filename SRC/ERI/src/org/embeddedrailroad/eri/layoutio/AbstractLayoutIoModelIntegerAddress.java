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

import com.crunchynoodles.util.TableOfBoolean;
import java.util.logging.Level;


/***
 *  Abstract implementation for {@link LayoutIoModel} with Integer.
 *
 * <p><strong>Remember, {@link byte} type is SIGNED!!</strong>
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
         *  assert : 'device' cannot be null.
         *  'newBits' as null means remove info about 'device'.
         */
        if( device == null )
            throw new NullPointerException("device cannot be null");

        //  'newBits' replaces prior.
        m_lock.writeLock().lock();
        try
        {
            if( newBits != null )
            {
                TableOfBoolean  d = m_inputs.get( device );
                if( null == d )
                {
                    //  First time here, create fresh TabelOfBoolean for this device.
                    d = new TableOfBoolean( newBits.length );
                    m_inputs.put( device, d );
                }
                d.setFrom(  newBits );
            }
            else
            {
                m_inputs.remove( device );
            }
        }
        catch( Throwable ex )
        {
            LOG.logp( Level.WARNING, this.getClass().getSimpleName(), "setSensedBinaryData", "cannot convert", ex );
        }
        finally
        {
            m_lock.writeLock().unlock();
        }
    }

    @Override
    public void setSensedBinaryData( Integer device, TableOfBoolean individual_bits )
    {

        if( device == null )
            throw new NullPointerException("device cannot be null");
        if( individual_bits == null )
            return;

        //  'individual_bits' OR in with prior.
        m_lock.writeLock().lock();
        try
        {
            TableOfBoolean  inputs = m_inputs.get( device );
            if( null == inputs )
            {
                //  first time for this device.  Make one quick and insert it into m_inputs !!
                inputs = new TableOfBoolean( individual_bits.size() );
                m_inputs.put( device, inputs );
            }

            inputs.setFrom( individual_bits );
        }
        catch( Throwable ex )
        {
            LOG.logp( Level.WARNING, this.getClass().getSimpleName(), "setSensedBinaryData", "Device=" + device.toString(), ex );
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
         *  E.g. device "6.22.19" with sub-function "3" has bytes from an RFID reader,
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
                m_blobs.put( device, new HashMap< Integer, byte[] >() );
            }
            HashMap< Integer, byte[] >  funct = m_blobs.get( device );

            //  2.  Copy the sub-function blob.
            if( blob != null )
            {
                funct.put( subf, Arrays.copyOf( blob, blob.length ) );
            }
            else
            {
                funct.remove( subf );
            }
        }
        catch( Throwable ex )
        {
            LOG.logp( Level.WARNING, this.getClass().getSimpleName(), "setSensedBinaryData", "Device=" + device.toString(), ex );
        }
        finally
        {
            m_lock.writeLock().unlock();
        }
    }

    //--------------------------  DATA GETTORS  --------------------------

    @Override
    public TableOfBoolean getSensedDataAll( Integer device )
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
            LOG.logp( Level.FINE, this.getClass().getSimpleName(), "getSensedDataAll", "Device=" + device.toString(), ex );
            return new TableOfBoolean();
        }

        return m_inputs.get( device );
    }

    @Override
    public boolean getSensedDataOne( Integer device, int bit_number )
            throws ArrayIndexOutOfBoundsException, UnknownLayoutUnitException, NullPointerException
    {
        TableOfBoolean   whole = getSensedDataAll( device );

        if( bit_number < 0 || bit_number >= whole.size() )
        {
            throw new ArrayIndexOutOfBoundsException( "getSensedDataOne(" + device.toString() +"," + bit_number + ") out-of-range" );
        }

        return whole.get( bit_number );
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
            LOG.logp( Level.WARNING, this.getClass().getSimpleName(), "getSensedBlob", "Device=" + device.toString(), ex );
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
    private final transient     ReadWriteLock   m_lock = new ReentrantReadWriteLock();

    /*** Store unit initialization strings, indexed by unit-address. */
    private final transient     HashMap< Integer, ArrayList<byte[]> >    m_init_msgs;

    private final transient     HashMap< Integer, byte[] >          m_query_msgs;

    /***
     *  HashMap of boolean input bits, indexed by device address.
     *  {@code m_inputs.get()} returns the whole array for one device.
     */
    private final transient     HashMap< Integer, TableOfBoolean >   m_inputs;

    /***
     *  Recorded data-blobs from different units: primary index = unit, secondary index = device therein.
     *  Data blobs are as big as given to use, and can vary in size.
     */
    private final transient     HashMap< Integer, HashMap< Integer, byte[] > >    m_blobs;

    /***  Logging output spigot. */
    private final transient static  Logger LOG = Logger.getLogger( AbstractLayoutIoModelIntegerAddress.class.getName() );

}
