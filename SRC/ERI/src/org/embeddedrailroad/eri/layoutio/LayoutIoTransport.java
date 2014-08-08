/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author brian
 */
public interface LayoutIoTransport
{
    /*** @return same name as IO model. */
    public String getName();

    /***
     *  Given channel setup properties, attempt to connect and use transport channel.
     *  If successful, call needs to invoke {@code setPolling(true)} to start running.
     *
     * @return {@code true} if attachment successful, {@code false} otherwise.
     */
    public boolean attach();

    /***
     *  Stop polling and disconnect the channel.  Afterwards, other clients can
     *  use said channel.  For serial ports, the COM port is closed.
     *  For TCP or UDP, the server socket is closed and freed.
     */
    public void detach();

    /*** Control polling of units via this single transport channel. */
    public void setPolling( boolean run_polling );

    /*** @return {@code true} if polling is active, {@code false} otherwise. */
    public boolean isPolling();


    public void setProperty( String prop_name, Object value );

    public Object getProperty( String prop_name );

    public Properties   getAllProperties();
}