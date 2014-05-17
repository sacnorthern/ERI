/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.block.interfaces;

import java.util.List;
import java.util.SortedMap;

/**
 *
 * @author brian
 */
public interface CtcBlockItem extends CtcModelItem {

    /*** Short block could also be an OS "on sheet" type of block. */
    public final static byte    LEN_SHORT = 1;
    public final static byte    LEN_MEDIUM = 2;
    public final static byte    LEN_REGULAR = 3;
    public final static byte    LEN_LONG = 4;

    /***
     * Direction-of-travel is unknown or not set.
     */
    public final static byte    DOT_UNKNOWN = 0;
    public final static byte    DOT_LEFT = 1;
    public final static byte    DOT_RIGHT = 2;

    /***
     * @return Approximate and relative length of this block.
     */
    public byte getApproximateLength();

    public final static int     SIG_LEFT = -1;
    public final static int     SIG_RIGHT = -2;

    /***
     *  {@codeMap[ SIG_LEFT]} is mainline-leftside, {@code Map[SIG_RIGHT}] is mainline-rightside.
     *  If there are diverging routes, then use {@code CtcSignalMastItem.ROUTE_ASPECT} to
     *  retrieve and corresponding signal for that route, which could be {@code null}.
     *  Map[0] is invalid.
     * @return Copy set of Signals protecting entry into this block, indexed by how connected.
     */
    public SortedMap<Integer, CtcSignalMastItem> getSignals();

    /***
     * List of turnouts inside of block, or {@code null} if none.
     * @return
     */
    public List<CtcTurnoutItem> getTurnouts();
}
