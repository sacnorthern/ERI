/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.Arrays;

/**
 *
 * @author brian
 * @param <TUnitAddr> Address-type for units of a communication protocol.
 */
public class LayoutIoDataChangedEvent< TUnitAddr > extends java.util.EventObject
{
    /***
     *  Copy old and new bit-arrays, and create an event.
     *
     * @param unit Address of unit that has changed bits to report.
     * @param old_bits what is was before, a copy is made.
     * @param new_bits what is now, a copy is made.
     */
    public LayoutIoDataChangedEvent( TUnitAddr unit, boolean[] old_bits, boolean[] new_bits )
    {
        super( unit );

        this.m_unit = unit;
        this.m_old  = Arrays.copyOf( old_bits, old_bits.length );
        this.m_new  = Arrays.copyOf( new_bits, new_bits.length );
    }

    public TUnitAddr    getUnitAddress()
    {
        return m_unit;
    }

    /***
     *  Returns bits the way they were, actually a copy so OK if you must modify.
     * @return bits previously.
     */
    public boolean[]    getOldBits()
    {
        return m_old;
    }

    /***
     *  Returns bits the way they are now, actually a copy so OK if you must modify.
     * @return Updated bits.
     */
    public boolean[]    getNewBits()
    {
        return m_new;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    TUnitAddr       m_unit;

    boolean[]       m_old;
    boolean[]       m_new;
}
