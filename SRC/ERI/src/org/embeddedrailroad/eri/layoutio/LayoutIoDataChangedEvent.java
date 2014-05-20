/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

/**
 *
 * @author brian
 */
public class LayoutIoDataChangedEvent< TUnitAddr > extends java.util.EventObject
{
    public LayoutIoDataChangedEvent( TUnitAddr unit, boolean[] old_bits, boolean[] new_bits )
    {
        super( unit );

        this.m_unit = unit;
        this.m_old  = old_bits;
        this.m_new  = new_bits;
    }

    public TUnitAddr    getUnitAddress()
    {
        return m_unit;
    }

    public boolean[]    getOldBits()
    {
        return m_old;
    }

    public boolean[]    getNewBits()
    {
        return m_new;
    }

    //--------------------------  INSTANCE VARS  -------------------------

    TUnitAddr       m_unit;

    boolean[]       m_old;
    boolean[]       m_new;
}
