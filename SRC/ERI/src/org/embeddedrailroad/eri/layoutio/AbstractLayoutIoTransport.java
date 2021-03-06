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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crunchynoodles.util.XmlPropertyBean;
import com.crunchynoodles.util.exceptions.UnsupportedKeyException;

/**
 *  Abstract class with partial implementation of {@link LayoutIoTransport}.
 *  Methods to control polling but no instance-var for it.
 *  Likely it is state information of some deep-down polling state-machine engine object.
 * <p>
 *  This class-type is also a properties-bean.
 * <p>
 *  See: http://tutorials.jenkov.com/java/abstract-classes.html
 *
 * @author brian
 */
public abstract class AbstractLayoutIoTransport implements LayoutIoTransport
{
    private AbstractLayoutIoTransport()
    {
        // who will actually use the no-args constructor?
    }

    protected AbstractLayoutIoTransport( LayoutIoProtocolProvider owner )
    {
        m_is_opened  = false;
        m_owner      = owner;
    }

    //------------------------  PROPERTY MANAGEMENT  --------------------------

    @Override
    public XmlPropertyBean getProperty( String prop_name )
    {
        return m_properties.get( prop_name );
    }

    @Override
    public void setProperty( String key, Object value )
    {
        String  simple_name;

        if( value.getClass().getSimpleName().endsWith( "[]") )
        {
            //  It's an array of something, go with hex-bytes as external representation.
            simple_name = "hexbytes";
        }
        else
        if( value instanceof String )
        {
            //  String might be list, and these have different type-specs.
            if( ((String)value).indexOf( (int) ';' ) > 0 )
                simple_name = "list";
            else
                simple_name = "string";
        }
        else
            if( value instanceof Integer )
                simple_name = "int";
        else
                if( value instanceof Float )
                    simple_name = "float";
        else
                    if( value instanceof Double )
                        simple_name = "double";
        else
                        if( value instanceof Boolean )
                            simple_name = "boolean";
        else
                            simple_name = "base64";

        _setProp( key, simple_name, value );
    }

    @Override
    public void setProperties( List< XmlPropertyBean >  propList )
            throws UnsupportedKeyException
    {
        for( XmlPropertyBean bean : propList )
        {
            _setProp( bean.getKey(), bean.getType(), bean.getValue() );
        }
    }

    protected void _setProp( String key, String typespec, Object value )
            throws UnsupportedKeyException
    {
        LOG.log( Level.INFO, String.format( "AbstractLayoutIoTransport#_setProp(%1$s, %2$s, VALUE)", key, typespec ) );

        for( String good_key : _getKnownPropertyKeys() )
        {
            if( good_key.equalsIgnoreCase( key ) )
            {
                m_properties.put( key, new XmlPropertyBean(key, typespec, value) );
                return ;
            }
        }

        throw new UnsupportedKeyException( key, true );
    }

    @Override
    public List< XmlPropertyBean >   getAllProperties()
    {
        return new ArrayList< XmlPropertyBean >( m_properties.values() );
    }

    /***
     *  Provides list of property keys this transport feels are important.
     * @return String array.
     */
    protected abstract String[] _getKnownPropertyKeys();


    //---------------------------  INSTANCE VARS  -----------------------------

    /***
     *  Data access READER/WRITER lock.
     *  @see https://www.obsidianscheduler.com/blog/java-concurrency-part-2-reentrant-locks/
     */
    transient protected final ReadWriteLock   m_lock = new ReentrantReadWriteLock();

    transient protected boolean       m_is_opened;

    protected HashMap< String, XmlPropertyBean >  m_properties = new HashMap<>();

    /*** IO Provider that we belong to. */
    transient protected LayoutIoProtocolProvider      m_owner;

    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( AbstractLayoutIoTransport.class.getName() );

}
