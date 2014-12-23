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
