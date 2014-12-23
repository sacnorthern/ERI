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

package org.embeddedrailroad.eri.layoutio;

import org.embeddedrailroad.eri.block.interfaces.CtcSignalMastItemListener;

/**
 *  The Layout Signal reads a Signal stored in {@link RailroadModel} and
 *  represents it by interpreting the current value(s) therein , and then
 *  commanding the LayoutIO sub-system to change.
 *
 *  This object represents one signal arm.  A signal mast has one or more arms.
 *
 *  This interface is independent of the actual Layout Model implementation,
 *  e.g. CMRI or CTI Electronics or DCC Accessory bus.
 *
 * @author brian
 */
public interface LayoutSignal extends CtcSignalMastItemListener
{

    /*** @return Name of signal arm we watch. */
    public String   getWatchesName();

    public void     setWatchesName( String plate_name_to_watch );

    /*** @return {@code} if signal can do blinking. */
    public boolean  getSignalBlinkable();

    /***
     *  Blinking, at say 55 blinks / minute , is not technically a property of
     *  this signal object.  Rather it is copied over from the {@code RailroadSignalItem}.
     *
     * @param can_blink {@code true} if blinking allowed.
     */
    public void     setSignalBlinkable( boolean can_blink );

    public void     enableBlink( boolean do_blinking );

    /***
     *  Sets the blink rate for all same signals.
     *  Defaults to system-wide value, so OK to be left unset in your program.
     *
     *  @return blinking rate , in blinks / minute.
     */
    public int      getBlinkRate();

    /***
     *  Sets the blinking rate, in flashes per minute
     *  @param blinks_per_minute rate
     */
    public void     setBlinkRate( int blinks_per_minute );

    //-----------------------  OUTPUT LINES (LEADS)  --------------------------

    /*** @return number of output lines required for all indications control. */
    public int      getTotalOutputLeads();

    /***
     *  Fetches the unit address object and output pin address object
     *  for some particular output lead.
     *
     * @param which 0 to MAX-1.
     * @param unit_and_output_addr caller provided array[2].
     * @throws IndexOutOfBoundsException if {@code which} is out-of-bounds.
     * @throws NullPointerException when {@code unit_and_ouput_addr} isn't big enough.
     */
    public void     getOutputLead( int which, Object unit_and_output_addr[] )
                throws IndexOutOfBoundsException, NullPointerException;

    /***
     *  Connects an output line for this signal to some particular , physical
     *  output.
     * @param which 0 to MAX-1.
     * @param unit_addr returned by IO model.
     * @param output_lead address object of a wire, e.g. type Integer for CMRI.
     * @throws IndexOutOfBoundsException if {@code which} is out-of-bounds.
     */
    public void     setOutputLead( int which, Object unit_addr, Object output_lead )
                throws IndexOutOfBoundsException;
}
