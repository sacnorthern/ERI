/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import com.crunchynoodles.eri.util.NumericSpan;
import java.util.UUID;

/**
 *  This interface has methods to create signals and signaling rules based on
 *  some prototype.  Users can create their own prototype, if they'd like.
 *
 *  Each "division" has its own {@code PrototypeStandards} object, so technically
 *  a layout could have two separate prototype flavors in use at the same time.
 *
 * @author brian
 */
public interface PrototypeStandards {

    //--------------------------  DESIGNER  ---------------------------

    /*** Short name of this prototype standard, e.g. "1940's SP" */
    public final static int   PROP_NAME_SHORT = 1;
    /*** Long name of this prototype standard, e.g. "B&O pre-CONrail merger with CPL's" */
    public final static int   PROP_NAME_LONG = 2;
    /*** Name or names of designers/implementors, e.g. "Chuck and Seth, and friends". */
    public final static int   PROP_DESIGNER_NAME = 3;
    /*** Release version as a string, e.g. "SP vers 5.3.1 (Dec 2009)" */
    public final static int   PROP_DESIGNER_VERSION = 4;
    /*** URL string of designer, ready to feed to {@link java.net.URL}, e.g. "http://www.spcoast.com/eri/models/" */
    public final static int   PROP_DESIGNER_URL = 5;
    /*** Approximate era as a string, e.g. "1940 - 1955" */
    public final static int   PROP_ERA = 6;

    /***
     *  Return a property string about this PrototypeStandards,
     *  e.g. PROP_NAME_LONG, PROP_DESIGNER_URL, or PROP_DESIGNER_VERISON.
     *  If unknown or unimplemented string, then returns {@code null}
     *  instead of throwing an exception.
     *
     * @param which {@code PROP_ZZZ} number.
     * @return string, or {@code null} if property unknown.
     */
    public String       getPropertyString( int which );

    /***
     *  Get identifier that uniquely identifies this prototype standard.
     *  Use for comparing equality.
     * @return many bits that should be universally unique.
     */
    public UUID         getVersionGuid();

    //---------------------------  GENERAL  ---------------------------

    public NumericSpan<Integer> getYearRange();

    //-----------------  SIGNALS  -----------------

    /***  Use USA West-coast style route signaling. */
    public final static int   SSF_ROUTING               = 1 << 0;
    /***  Use white position lights. */
    public final static int   SSF_POSITION_LIGHT        = 1 << 1;
    /***  Use colored position lights. */
    public final static int   SSF_COLOR_POSITION_LIGHT  = 1 << 2;
    /***  Use upper-quadrant semaphores. */
    public final static int   SSF_SEMAPHORE_UPPERS      = 1 << 3;
    /***  Use lower-quadrant semaphores. */
    public final static int   SSF_SEMAPHORE_LOWER       = 1 << 4;
    /***  Use German Railway signals. */
    public final static int   SSF_GERMAN                = 1 << 5;
    /***  Use English Railway signals. */
    public final static int   SSF_ENGLAND               = 1 << 6;
    /***  Use Chinese Railway signals. */
    public final static int   SSF_CHINA                 = 1 << 7;
    /***  Use speed signaling. */
    public final static int   SSF_SPEED_SIGNALS         = 1 << 8;

    /***
     *  Returns flags describing the primary signal style ( and implying rules ).
     *  Because of mergers and particular tastes, it is possible to have many.
     *  The choices here relate to the "common signal choices" available to the
     *  layout designer.
     *
     * @return one or more of {@code SFF_ZZZ} flags.
     */
    public int      getPrimarySignalStyleFlags();

    /***
     *  Return flags describing all signal styles that can be deployed on the layout.
     *  These are primary and secondary.
     *  Only these choices are offered to the layout designer.
     *  {@code getPrimarySignalStyleFlags() &lt;= {@code getEverywhereSignalStyleFlags()]
     *
     * @return one or more of {@code SFF_ZZZ} flags.
     */
    public int      getEverywhereSignalStyleFlags();

}
