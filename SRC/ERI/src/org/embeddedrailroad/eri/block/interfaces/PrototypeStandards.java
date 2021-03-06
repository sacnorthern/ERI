/***  Java-ERI    Java-based Embedded Railroad Interfacing.
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
 ***
 ***  Licensed under the Apache License, Version 2.0 ( the "License" ) ;
 ***  you may not use this file except in compliance with the License.
 ***  You may obtain a copy of the License at:
 ***        http://www.apache.org/licenses/LICENSE-2.0
 ***
 ***  Unless required by applicable law or agreed to in writing, software
 ***  distributed under the License is distributed on an "AS IS" BASIS,
 ***  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ***  See the License for the specific language governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.block.interfaces;

import com.crunchynoodles.util.NumericSpan;
import java.util.UUID;

/**
 *  This interface has methods to create signals and signaling rules based on
 *  some prototype.  Users can create their own prototype, if they'd like.
 *
 *  Each "division" has its own {@code PrototypeStandards} object, so technically
 *  a railroad could have two separate prototype flavors in use at the same time.
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
    /*** URL string of designer, ready to feed to {@link java.net.URL} class constructor, e.g. "http://www.spcoast.com/eri/models/" */
    public final static int   PROP_DESIGNER_URL = 5;
    /*** Approximate era as a string, e.g. "1940 - 1955" */
    public final static int   PROP_ERA = 6;
    /*** Measuring in miles "M" or kilometers "K". */
    public final static int   PROP_USE_MILES = 7;

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
    public final static int   SSF_GERMANY               = 1 << 5;
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
     *  Return flags describing all signal styles that can be deployed on the railroad.
     *  These are primary and secondary.
     *  Only these choices are offered to the layout designer.
     *  {@code #getPrimarySignalStyleFlags()} is-subset-of {@code #getEverywhereSignalStyleFlags()]}
     *
     * @return one or more of {@code SFF_ZZZ} flags.
     */
    public int      getEverywhereSignalStyleFlags();

}
