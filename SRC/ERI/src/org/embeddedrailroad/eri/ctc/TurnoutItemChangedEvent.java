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

import org.embeddedrailroad.eri.block.interfaces.CtcTurnoutItem;

/**
 *
 * @author brian
 */
public class TurnoutItemChangedEvent extends java.util.EventObject
{
    /*** event cause : turnout added to model. */
    public final static int  CUZ_ADD_TO_MODEL           = 1 << 0;
    /*** Asking for a possible veto... */
    public final static int  CUZ_CHECKING_VETO          = 1 << 1;
    /*** There were no vetoes so now changing... */
    public final static int  CUZ_NOW_INDETERMINATE      = 1 << 2;
    /*** Change is done, turnout has different selection. */
    public final static int  CUZ_STABLE                 = 1 << 3;

    /***
     *  Something about a turnout has changed.
     *  Upon creation, a change-event is generated first with {@link #CUZ_ADD_TO_MODEL}
     *  then {@link #CUZ_NOW_INDETERMINATE}.
     * @param turnout Object of change.
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
