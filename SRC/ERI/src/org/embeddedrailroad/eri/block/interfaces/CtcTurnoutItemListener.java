/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import java.util.EventListener;
import org.embeddedrailroad.eri.ctc.TurnoutItemChangedEvent;

/**
 *
 * @author brian
 */
public interface CtcTurnoutItemListener extends EventListener {

    /*** Turnout update, general */
    public void ctcTurnoutChanged( TurnoutItemChangedEvent evt );

    /***
     *  Ask listeners if they want to veto a turnout change.
     *  The {@see TurnoutItemChangedEvent} object is not in main Model (but could be in a proxy).
     *
     * @param evt has detail of the turnout with desired position.
     * @return {@code true} to veto and stop evaluating, {@code false} to continue asking.
     */
    public boolean ctcTurnoutAskVeto( TurnoutItemChangedEvent evt );
}
