/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

/**
 *  A searchlight signal has one opening in its face for light to be shown out.
 *  The prototype searchlight signal is one white lamp with three filters in
 *  a movable frame placed in front of the white lamp.
 *  The model equivalent can use a white light and color filters that shine
 *  into a "light pipe" to transmit the color up to the signal arm.
 *
 *  More likely, the searchlight is a single LED, having either 2, 3 or 4 leads.
 *  The 2 lead version is call "bi-color" and the color emitted depends on polarity
 *  of current across the leads: plus-minus for red, minus-plus for green.  Yellow
 *  can be created by rapidly flashing between the two colors, like at 12KHz.
 *
 *  The other kind has 3 leads: one is common, with one lead for red and the other
 *  led connected to the green LED.  This means both LEDs can be on at the same
 *  time, compared to the 2 lead LED where only one is on at a time.
 *  Both 2 and 3 lead LEDs can be flashed on and off rapidly to create yellow.
 *  When blending two colors to make a third, with some brands, the human eye
 *  can discern a red spot-of-light separate from a yellow spot-of-light.
 *  This is noticed when the LED housing does not diffuse well, and the two
 *  emitters are not right next to each other.
 *
 *  The last kind has 4 leads: one for the colors red, yellow and green, and the
 *  fourth lead for the common.  We will call it "tri-color".
 *  These kind do not require rapid flashing to create
 *  yellow.  Instead of the LED die has a yellow emitter , along with the red and
 *  green emitters.  Its advantage is the color ( wavelength ) of the yellow is
 *  well known.
 *
 * @author brian
 */
public interface LayoutSignalSearchlightBiColor extends LayoutSignal
{

    public final static int     LEAD_RED_ON = 0;
    public final static int     LEAD_GREEN_ON = 1;
    public final static int     LEAD_BLINK_ON = 2;

    /***
     * @return {@code true} if I/O unit will rapidly flicker LED to make yellow.
     */
    public boolean  getUnitImplementsFlash();

    /***
     *  Sets whether or not the I/O unit can quickly flash red/green together
     *  to make a yellow light.  Implies a 2 lead, bi-color LED is used and
     *  the signal style is searchlight.
     *
     * @param unit_does_flash
     */
    public void     setUnitImplementsFlash( boolean unit_does_flash );


}
