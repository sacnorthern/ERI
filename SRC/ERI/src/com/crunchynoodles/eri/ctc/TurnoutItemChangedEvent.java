/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.ctc;

import com.crunchynoodles.eri.block.interfaces.CtcTurnoutItem;

/**
 *
 * @author brian
 */
public class TurnoutItemChangedEvent extends java.util.EventObject
{
    public final static int  CUZ_ADD_TO_MODEL           = 1 << 0;
    /*** Asking for a possible veto... */
    public final static int  CUZ_CHECKING_VETO          = 1 << 1;
    /*** There were no vetoes so now changing... */
    public final static int  CUZ_NOW_INDETERMINATE      = 1 << 2;
    /*** Change is done, turnout has different selection. */
    public final static int  CUZ_STABLE                 = 1 << 3;

    /***
     *  Something about a turnout has changed.
     *  Upon creation, a change-event is generated first with {@link CUZ_ADD_TO_MODEL}
     *  then {@link CUZ_NOW_INDETERMINATE}.
     * @param block Object of change.
     * @param changeFlags See CUZ_XXX defines above.
     */
    public TurnoutItemChangedEvent( CtcTurnoutItem turnout, int changeFlags )
    {
        super( turnout );
        m_reasons = changeFlags;
    }

    @Override
    public String toString()
    {
        StringBuilder  sb = new StringBuilder();
        sb.append( "{turnout " );
        sb.append( getTurnout().getName() );
        sb.append( " changed by [" );

        String  sep = "";

        if( (m_reasons & CUZ_ADD_TO_MODEL) != 0 )
        {
            sb.append( sep );
            sb.append( "New" );
            sep = "+";
        }
        if( (m_reasons & CUZ_CHECKING_VETO) != 0 )
        {
            sb.append( sep );
            sb.append( "CheckVeto" );
            sep = "+";
        }
        if( (m_reasons & CUZ_NOW_INDETERMINATE) != 0 )
        {
            sb.append( sep );
            sb.append( "Indeterminate" );
            sep = "+";
        }
        if( (m_reasons & CUZ_STABLE) != 0 )
        {
            sb.append( sep );
            sb.append( "Stable" );
            sep = "+";
        }

        //  If separator is still empty-string, then we don't know the reason.
        if( sep.length() == 0 )
        {
            sb.append( "?misc?" );
        }

        sb.append(  "]}" );

        return sb.toString();
    }

    public CtcTurnoutItem  getTurnout() { return (CtcTurnoutItem) this.source; }

    //--------------------------  INSTANCE VARS  -------------------------

    private int             m_reasons;


}
