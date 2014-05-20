/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

/**
 *  A turnout in the CTC model of the layout.  A turnout is part (or all) of a block.
 *  A route can be locked, in which case it cannot change.
 *
 *  There are up to 31 different reasons to lock a turnout, 0 to 30.
 *  Each reason-to-lock value must be coordinated so as not to overlap.
 *
 *  When a turnout changes, it first asks if there are any vetoes.  If none, then
 *  the state changes to indeterminate.  After a short while, the turnout announces
 *  its new route.
 *
 * @author brian
 */
public interface CtcTurnoutItem extends CtcModelItem {

    //-------------------------  JDK 7 Enums  --------------------------

    public enum TURNOUT_LEADS_TO {
        MAIN(10),
        CROSSOVER(8),
        PASSING(6),
        YARD(2),
        SPUR(0);

        public byte  m_speed;

        private TURNOUT_LEADS_TO( int v )
        {
            this.m_speed = (byte) v;
        }
    };

    public enum TURNOUT_DIVERAGE_DIRECTION {
        UNKNOWN(0),
        LEFT(1),
        CENTER(2),
        RIGHT(3);

        public byte  m_direction;

        private TURNOUT_DIVERAGE_DIRECTION( int v )
        {
            this.m_direction = (byte) v;
        }
    }

    //---------------------------  BLOCKS  ----------------------------

    /***
     * @return Block where turnout lives.
     */
    public CtcBlockItem  getBlock();

    /***
     * @return  track-type when facing the points.
     */
    public TURNOUT_LEADS_TO getFacingLead();

    /***
     * @return  track-type when Turnout set to route #1.
     */
    public TURNOUT_LEADS_TO getTrailingLead();

    /***
     * @return  track-type when Turnout set to route #2.
     */
    public TURNOUT_LEADS_TO getDiveringLead();

    /***
     * @param which route, 1 to MAX.
     * @return track type of route
     */
    public TURNOUT_LEADS_TO getRouteLead( int which );

    /***
     *  Return diverging direction ( orientation ).  Route #1 is
     *  always straight.
     * @param which route, 1 to MAX.
     * @return direction as viewed from facing-points, or UNKNOWN if unset.
     */
    public TURNOUT_DIVERAGE_DIRECTION getDiverageDirection( int which );

    //---------------------------  ROUTES  ----------------------------

    /***
     * @return MAX routes possible.  Route 1 is straight-most.
     */
    public int getMaxRoutes();

    /***
     * @return Current route set to, 1 to MAX, else 0 if indeterminate.
     */
    public int getCurrentRoute();

    /***
     *  Set the route for turnout.  0 = makes indeterminate.
     *  If any lock-flags are set, change is rejected.
     * @return -1 if any locks set, else @{code which} when accepted.
     */
    public int setRoute( int which );

    //------------------------  ROUTE LOCKS  -------------------------

    /***
     * @return Return bitmask of reasons turnout is locked, 0 = none.
     */
    public int getLockingFlags();

    /***
     *  Remove some locking reasons.  Once all locks removed, then turnout can change.
     *  Locks do NOT nest.
     * @param flag usually a single bit is set.
     */
    public void clearLockingFlag( int flag );

    /***
     *  Set some locking reasons.  If a reason is already set, there is no change.
     *  Locks do NOT nest.
     * @param flag
     */
    public void setLockingFlag( int flag );

    //------------------------  ROUTE SPEED  --------------------------

    /***
     *  Ask for maximum speed of some route over a turnout.
     *  Assumes direction of travel is from facing-point over turnout.
     *  Asking for max-speed does not change the turnout's alignment.
     *  E.g. if which=1 is passing, then could return {@code CtcSignalMastItem.SPEED_CHOICE.APPROACH}
     *  and which=2 is yard, then could return {@code CtcSignalMastItem.SPEED_CHOICE.RESTRICTING}.
     *
     * @param which route, 1 for straight, &gt; 1 for diverging ones.
     * @return maximum speed thru route.
     */
    public CtcSignalMastItem.SPEED_CHOICE getMaxRouteSpeed( int which );

}
