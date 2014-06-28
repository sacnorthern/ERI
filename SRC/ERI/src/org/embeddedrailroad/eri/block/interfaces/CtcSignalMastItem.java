/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

/**
 *  A "signal mast" governs one track.  A mast consists of one or more "signal heads".
 *  Each signal head can be
 * <ol>
 *   <li> a multi-colored light (e.g. a searchlight),
 *   <li> a movable semaphore,
 *   <li> a collection of colored lights, one illuminated at a time,
 *   <li> a collection of white lights arranged in a pattern-group,
 *   <li> a collection of colored lights arranged in a pattern-group
 *          (e.g. B&amp;O color position lights (CPL)).
 * </ol>
 *
 *  If a marker light appears on the mast, it could be an upper-marker or lower-marker.
 *  Markers have a single color, or can be dark.
 *  For B&amplO-type signaling, two markers are part of the signal aspect.
 *
 * @author brian
 * @see http://jmri.sourceforge.net/help/en/html/tools/signaling/index.shtml
 */
public interface CtcSignalMastItem extends CtcModelItem {

    //-------------------------  JDK 7 Enums  --------------------------

    /*** The larger the number, the more diverging is route and slower train/engine must go. */
    public enum ROUTE_CHOICE {
        NONE(0),
        DIVERAGE_1(1),
        DIVERAGE_2(2),
        DIVERAGE_3(3),
        DIVERAGE_4(4),
        DIVERAGE_5(5),
        DIVERAGE_6(6),
        DIVERAGE_7(7),
        DIVERAGE_8(8),
        DIVERAGE_9(9),
        DIVERAGE_10(10),
        DIVERAGE_11(11),
        DIVERAGE_12(12),
        DIVERAGE_13(13),
        DIVERAGE_14(14),
        DIVERAGE_15(15),
        DIVERAGE_16(16),
        DIVERAGE_17(17),
        DIVERAGE_18(18),
        DIVERAGE_19(19),

        CLEAR(200);

        private byte  m_route;

        private ROUTE_CHOICE(int r)
        {
            this.m_route = (byte) r;
        }

        public int value()
        {
            return this.m_route;
        }
    }

    /***
     *  Indicate speed for the route ahead.
     *  Depending on prototype signaling rules and number of signal-heads,
     *  some of these indications cannot be displayed.
     *
     *  The bigger the number, the faster you can go!
     */
    public enum SPEED_CHOICE {
        STOP(0),
        STOP_AND_PROCEED(1),
        RESTRICTING(2),
        SLOW_APPROACH(3),
        SLOW_CLEAR(4),
        LIMITED_APPROACH(5),
        MEDIUM_APPROACH(6),
        MEDIUM_APPROACH_MEDIUM(7),
        APPROACH(8),
        APPROACH_SLOW(9),
        LIMITED_CLEAR(10),
        MEDIUM_CLEAR(11),
        APPROACH_LIMITED(12),
        APPROACH_MEDIUM(13),
        CLEAR(14);

        private byte    m_speed;

        private SPEED_CHOICE( int s )
        {
            this.m_speed = (byte) s;
        }
    }

    public ROUTE_CHOICE     getEffectiveRouteAspect();

    public ROUTE_CHOICE[]   getRouteAspects();

    public SPEED_CHOICE     getEffectiveSpeedAspect();

    //---------------------------  BLOCKS  ----------------------------

    /***
     * @return Block to which signal protects entry.
     */
    public CtcBlockItem  getProtectingBlock();

    /***
     * @return milage as a decimal number.
     */
    public float    getMilepost();

    public void     setMilepost( float milage );

    //-------------------------  FINER POINTS  --------------------------

    /***
     * @return {@code true} if signal is absolute, i.e. STOP cannot be passed without permission.
     */
    public boolean isAbsolute();

    public void setIsAbsolute( boolean absolute );

    /***
     * @return {@code true} if signal "is in the distant" to signal in advance of here.
     */
    public boolean isDistant();

    public void setIsDistant( boolean distant );

    /***
     * @return {@code true} if signal is on non-level grade.
     */
    public boolean isGrade();

    public void setIsGrade( boolean grade );

    /***
     * @return count of lights on this signal mast.
     */
    public int  getHeadCount();

    public void setHeadCount( int heads );

    //---------------------------  MUTATORS  ----------------------------

    /***
     *  Set signal to full-stop, all signals STOP.
     */
    public void setAllStop();

    /***
     *  Make signal the most restrictive of current setting and {@code aspect}.
     *  If changed, then updates listeners.
     * @param aspect Must be at most this.
     */
    public void minimizeAspect( SPEED_CHOICE aspect );

    /***
     *  Take speed down one notch.
     */
    public void restrict();

    /***
     *  Sets whether this signal can display a certain speed aspect or not.
     * @param which Aspect to control
     * @param has_it {@code true} if can display, else {@code false} if can't.
     */
    public void addRemoveAspect( SPEED_CHOICE which, boolean has_it );

    /***
     *  Determines if signal can display a specific speed aspect.
     * @param which Speed aspect to check on
     * @return {@code true} if can display, else {@code false} if can't.
     */
    public boolean checkCanDisplayAspect( SPEED_CHOICE which );

}
