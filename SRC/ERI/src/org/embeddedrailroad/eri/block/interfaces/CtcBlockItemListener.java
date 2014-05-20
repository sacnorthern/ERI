/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import org.embeddedrailroad.eri.ctc.BlockItemChangedEvent;
import java.util.EventListener;

/**
 * @author brian
 */
public interface CtcBlockItemListener extends EventListener {

    /***  Invoked when details of a block have changed. */
    public void ctcBlockChanged( BlockItemChangedEvent evt );
}
