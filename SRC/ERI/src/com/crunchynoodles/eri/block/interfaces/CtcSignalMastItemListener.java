/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.block.interfaces;

import java.util.EventListener;
import com.crunchynoodles.eri.ctc.SignalMastItemChangedEvent;

/**
 *
 * @author brian
 */
public interface CtcSignalMastItemListener extends EventListener {

    /*** Called when something about a signal-mast changes. */
    public void ctcSignalChanged( SignalMastItemChangedEvent evt );
}
