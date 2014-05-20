/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import org.embeddedrailroad.eri.block.interfaces.CtcBlockItem;

/**
 *  Something about a block has changed.  Could be Direction-of-Travel (DOT) or
 *  occupancy detection.
 * @author brian
 */
public class BlockItemChangedEvent extends java.util.EventObject
{

    public final static int  CUZ_ADD_TO_MODEL           = 1 << 0;
    public final static int  CUZ_OCCUPANCY              = 1 << 1;
    public final static int  CUZ_DIRECTION_OF_TRAVEL    = 1 << 2;
    public final static int  CUZ_TURNOUTS               = 1 << 3;

    /***
     *  Something about a block has changed.
     *  Upon creation, a {@link CUZ_ADD_TO_MODEL} event is generated.
     * @param block Object of change.
     * @param changeFlags See CUZ_XXX defines above.
     */
    public BlockItemChangedEvent( CtcBlockItem block, int changeFlags )
    {
        super( block );
        m_reasons = changeFlags;
    }

    @Override
    public String toString()
    {
        StringBuilder  sb = new StringBuilder();
        sb.append( "{block " );
        sb.append( getBlock().getName() );
        sb.append( " changed by [" );

        String  sep = "";

        if( (m_reasons & CUZ_ADD_TO_MODEL) != 0 )
        {
            sb.append( sep );
            sb.append( "New" );
            sep = "+";
        }
        if( (m_reasons & CUZ_OCCUPANCY) != 0 )
        {
            sb.append( sep );
            sb.append( "Occupancy" );
            sep = "+";
        }
        if( (m_reasons & CUZ_DIRECTION_OF_TRAVEL) != 0 )
        {
            sb.append( sep );
            sb.append( "DOT" );
            sep = "+";
        }
        if( (m_reasons & CUZ_TURNOUTS) != 0 )
        {
            sb.append( sep );
            sb.append( "Turnout" );
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

    public CtcBlockItem  getBlock() { return (CtcBlockItem) this.source; }


    private int             m_reasons;
}
