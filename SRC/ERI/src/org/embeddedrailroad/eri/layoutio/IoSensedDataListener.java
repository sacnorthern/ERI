/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.EventListener;

/**
 *
 * @author brian
 */
public interface IoSensedDataListener extends EventListener {

    public void     ioDataChanged( Object device_addr, boolean[] old_bits, boolean[] new_bits );

    public void     ioBlobChanged( Object device_addr, boolean[] old_bits, boolean[] new_bits );

}
