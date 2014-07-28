/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import org.embeddedrailroad.eri.block.interfaces.CtcSignalMastItem;

/**
 *
 * @author brian
 */
public class SignalMastItemChangedEvent extends java.util.EventObject
{
    public final static int  CUZ_ADD_TO_MODEL            = 1 << 0;
    public final static int  CUZ_WENT_DARK              = 1 << 1;
    public final static int  CUZ_WENT_ACTIVE            = 1 << 2;
    public final static int  CUZ_INDICATION_CHANGED     = 1 << 3;

    /***
     *  Something about a block has changed.
     *  Upon creation, a change-event is generated of {@code CUZ_ADD_TO_MODEL}.
     * @param signal Object of change.
     * @param changeFlags See CUZ_XXX defines above.
     */
    public SignalMastItemChangedEvent( CtcSignalMastItem signal, int changeFlags )
    {
        super( signal );
        m_reasons = changeFlags;
    }

    @Override
    public String toString()
    {
        StringBuilder  sb = new StringBuilder();
        sb.append( "{signal " );
        sb.append( getSignal().getName() );
        sb.append( " changed by [" );

        String  sep = "";

        if( (m_reasons & CUZ_ADD_TO_MODEL) != 0 )
        {
            sb.append( sep );
            sb.append( "New" );
            sep = "+";
        }
        if( (m_reasons & CUZ_WENT_DARK) != 0 )
        {
            sb.append( sep );
            sb.append( "Dark" );
            sep = "+";
        }
        if( (m_reasons & CUZ_WENT_ACTIVE) != 0 )
        {
            sb.append( sep );
            sb.append( "Active" );
            sep = "+";
        }
        if( (m_reasons & CUZ_INDICATION_CHANGED) != 0 )
        {
            sb.append( sep );
            sb.append( "Changed" );
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

    public CtcSignalMastItem  getSignal() { return (CtcSignalMastItem) this.source; }


    private int             m_reasons;

}
