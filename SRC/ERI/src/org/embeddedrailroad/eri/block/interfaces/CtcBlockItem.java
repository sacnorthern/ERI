/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import java.util.List;
import java.util.SortedMap;

/**
 *  A block on a CTC panel.  Blocks has detection to indicate a presence of train/equipment or not.
 *  A block has signal protection at all entrances.
 *
 * @author brian
 */
public interface CtcBlockItem extends CtcModelItem {

    /*  Block length-codes are ordered.  I.e LEN_SHORT < LEN_LONG. */
    public final static byte    LEN_SHORT = 1;      /*** Short block could also be an OS "on sheet" type of block. */
    public final static byte    LEN_MEDIUM = 2;     /*** Not short, yet not a full-length block. */
    public final static byte    LEN_REGULAR = 3;    /*** Regular holds a nominal-sized train. */
    public final static byte    LEN_LONG = 4;       /*** Holds more than one train! */

    public final static int     SIG_LEFT = -1;      /*** Left = EAST or SOUTH */
    public final static int     SIG_RIGHT = -2;     /*** Right = WEST or NORTH */

    public final static byte    DOT_UNKNOWN = 0;    /*** Unknown direction of travel. */
    public final static byte    DOT_LEFT = 1;       /*** Left = EAST or SOUTH */
    public final static byte    DOT_RIGHT = 2;      /*** Right = WEST or NORTH */

    /***
     * @return Approximate and relative length of this block.
     */
    public byte getApproximateLength();

    /***
     *  {@code Map[SIG_RIGHT}] is mainline-rightside, {@code Map[SIG_LEFT]} is mainline-leftside.
     *  If there are diverging routes, then use {@code CtcSignalMastItem.ROUTE_ASPECT} to
     *  retrieve any corresponding signal for that route, which could be {@code null}.
     *  Map[0] is invalid.
     *
     * @return Copy set of Signals protecting entry into this block, indexed by how connected.
     */
    public SortedMap<Integer, CtcSignalMastItem> getSignals();

    /***
     * List of turnouts inside of block, or {@code null} if none.
     * @return
     */
    public List<CtcTurnoutItem> getTurnouts();
}
