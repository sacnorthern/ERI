/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *   Singleton object that knows about all the physical I/O transports,
 *   i.e. protocols.
 *
 *   REQUIRES JAVA 1.5 OR LATER FOR CORRECT 'static volatile' IMPLEMENTATION.
 *
 * @author brian
 */
public class IoTransportManager
{
    private IoTransportManager()
    {
        LOG.setLevel( Level.ALL );
    }

    /***
     * static method to get instance
     * @return singleton instance of manager
     * @see http://stackoverflow.com/questions/18093735/double-checked-locking-in-singleton
     */
    public static IoTransportManager getInstance()
    {
        if (s_instance == null) { // first time lock
            synchronized (IoTransportManager.class) {
                if (s_instance == null) {  // second time lock
                    s_instance = new IoTransportManager();
                }
            }
        }
        return s_instance;
    }

    //----------------------  TRANSPORT ADD/REMOVE  -------------------

    /***
     *    Add a transport IO provider to known list.
     *
     *   @param name short name of provider / keeper.
     *   @param long_descr longer descriptive text, in EN_us.
     *   @param prov Provider of objects to give to transport
     *   @param trans I/O protocol transport.
     */
    public void addProviderTransport( String name, String long_descr, LayoutIoProvider prov, LayoutIoTransport trans )
    {
        ProviderTransportStruct  pts = new ProviderTransportStruct( long_descr, prov, trans );

        //  Remove first ~ just in case ~ then add new provider/keeper. */
        removeProviderTransport( name );
        m_providers.put( name, pts );

        LOG.log( Level.INFO, "Transport \"{0}\" now registered", name );
    }

    public void removeProviderTransport( String any_case_name )
    {
        String  proper_name = _findProviderByName( any_case_name );
        if( proper_name != null )
        {
            m_providers.remove( proper_name );
        }
    }

    /***
     *  Create a copy of the provider / keeper mappings.
     * @return copy of known mappings.
     */
    public HashMap< String, ProviderTransportStruct >  getProviderTransportList()
    {
        HashMap< String, ProviderTransportStruct >  ret_map = new HashMap<>();

        m_providers.putAll( ret_map );

        return ret_map;
    }

    /***
     *  Search for a provider by name.  Ignores letter-case in doing search.
     *  Returns copy so caller can do with it as they please.
     *
     * @param any_case_name Name of provider
     * @return null if not found, or copy of whole structure on match.
     */
    public ProviderTransportStruct  findProviderByName( String any_case_name )
    {
        String  proper_name = _findProviderByName( any_case_name );
        if( proper_name == null )
            return null;

        return new ProviderTransportStruct( m_providers.get( proper_name ) );
    }

    /***
     *   Retrieve a provider by name, ignoring string case.
     * @param any_case_name
     * @return Name ( with proper letter-case ) used by the provider.
     */
    protected String _findProviderByName( String any_case_name )
    {
        for( Map.Entry< String, ProviderTransportStruct > ent : m_providers.entrySet() )
        {
            if( ent.getKey().equalsIgnoreCase( any_case_name ) )
            {
                return ent.getKey();
            }
        }

        //  Oh well, not found.
        return null;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    public class ProviderTransportStruct
    {
        public String               longDescr;
        public LayoutIoProvider     provider;
        public LayoutIoTransport    transport;

        public ProviderTransportStruct(String longDescr, LayoutIoProvider provider, LayoutIoTransport transport )
        {
            this.longDescr = longDescr;
            this.provider  = provider;
            this.transport = transport;
        }

        public ProviderTransportStruct( ProviderTransportStruct other )
        {
            this.longDescr = other.longDescr;
            this.provider  = other.provider;
            this.transport = other.transport;
        }
    };

    /*** Store mappings of know I/O transports and their providers/keepers. */
    protected HashMap<String, ProviderTransportStruct>    m_providers = new HashMap<>();

    /*** Store singleton of transport manager. */
    private static volatile IoTransportManager  s_instance;

    private static final Logger LOG = Logger.getLogger( IoTransportManager.class.getName() );
}
