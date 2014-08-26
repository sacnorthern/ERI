/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *   Singleton object that knows about all the physical I/O transport providers,
 *   i.e. protocols.
 *<p>
 *   <b>NOTE: REQUIRES JAVA 1.5 OR LATER FOR CORRECT 'static volatile' IMPLEMENTATION.</b>
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
     *    Add a transport IO provider class to known list.
     *
     *   @param name short name of provider / keeper.
     *   @param longDescr longer descriptive text, in EN_us.
     *   @param providerClassRef Class ref to provider of objects to give to transport
     */
    public void addProvider( String name, String longDescr, Class<? extends LayoutIoProvider> providerClassRef )
    {

        ProviderTransportStruct  pts = new ProviderTransportStruct( name, longDescr, providerClassRef, null );

        //  Remove it first ~ just in case ~ then add new provider/keeper.
        removeProviderTransport( name );
        m_providers.put( name, pts );

        LOG.log( Level.INFO, "addProvider() Transport \"{0}\" now registered", name );
    }

    public void removeProviderTransport( String any_case_name )
    {
        String  proper_name = _findProviderByName( any_case_name );
        if( proper_name != null )
        {
            m_providers.remove( proper_name );
        }
    }

    //-------------------------  LISTING OF PROVIDERS  ------------------------

    /***
     *  Create a copy of the provider / keeper mappings.
     *  The mapping structure is cloned; strings are immutable.
     *
     * @return copy of known mappings.
     */
    public HashMap< String, ProviderTransportStruct >  getProviderTransportList()
    {
        HashMap< String, ProviderTransportStruct >  ret_map = new HashMap<>();

        Collection< ProviderTransportStruct > source = m_providers.values();
        for( ProviderTransportStruct s : source )
        {
            ret_map.put( s.shortName, s );
        }

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
     * @return Name ( with proper letter-case ) used by the provider, or null if no match.
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

        //  Oh well, not found...
        return null;
    }

    //--------------------------  HELPER CLASS  -------------------------------

    public class ProviderTransportStruct
    {
        public String               shortName;      // key.
        public String               longDescr;
        public Class<? extends LayoutIoProvider>  provider;
        public LayoutIoTransport    transport;

        public ProviderTransportStruct( String shortName,
                                        String longDescr,
                                        Class<? extends LayoutIoProvider> provider,
                                        LayoutIoTransport transport )
        {
            this.shortName = shortName;
            this.longDescr = longDescr;
            this.provider  = provider;
            this.transport = transport;
        }

        public ProviderTransportStruct( ProviderTransportStruct other )
        {
            this.shortName = other.shortName;
            this.longDescr = other.longDescr;
            this.provider  = other.provider;
            this.transport = other.transport;
        }
    };

    //---------------------------  INSTANCE VARS  -----------------------------

    /***
     *  Store mappings of known I/O transports and their providers/keepers.
     *  key = 'shortName'
     */
    protected HashMap<String, ProviderTransportStruct>    m_providers = new HashMap<>();

    /*** Store singleton of transport manager. */
    private static volatile IoTransportManager  s_instance;

    /*** Logging output spigot. */
    private static final Logger LOG = Logger.getLogger( IoTransportManager.class.getName() );
}
