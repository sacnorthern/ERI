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
