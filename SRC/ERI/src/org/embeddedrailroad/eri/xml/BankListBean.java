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
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/***
 *  A <b>bankList</b> holds a list of communications banks.  Each bank has a connection
 *  e.g. TCP on port 12345 or COM1 at 9600,8,E,1.
 *  Check element {@code bankList > Bank > comms} for details. <p>
 *
 *  For a not-large layout, there is usually just one bank, but may be 2. <p>
 *
 *  {@code <!ELEMENT bankList (bank*) >}
 * @author brian
 */
public class BankListBean
    implements XmlEntityBean
{
    public static final String PROP_ELEMENT_NAME = "bankList";

    @Override
    public String getElementName() {
        return PROP_ELEMENT_NAME;
    }

    @Override
    public List<String> getAttributeList() {
        // null means there are no attributes.
        return null;
    }

    public BankListBean()
    {
        m_bank_list = new ArrayList<BankBean>( 2 );
    }

    // ----------------------------------------------------------------------------

    @Override
    public int  hashCode()
    {
        int  hc = super.hashCode();

        if( m_bank_list != null )   hc ^= m_bank_list.hashCode();

        return( hc );
    }

    @Override
    public String  toString()
    {
        StringBuilder  sb = new StringBuilder( 100 );

        sb.append( "BankListBean:[" );

        sb.append( "m_bank_list={" );
        if( m_bank_list != null )
            sb.append( m_bank_list.toString() );
        else
            sb.append( NULL_OBJECT_REF_STRING );

        sb.append( "}]" );

        return( sb.toString() );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final BankListBean other = (BankListBean) obj;
        if( ! this.m_bank_list.equals( other.m_bank_list ) ) {
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------------

    public static final String PROP_BANK = "bank";      // element

    public void setBankList( List<BankBean> bank_list )
    {
        m_bank_list = null;
        m_bank_list = new ArrayList<BankBean>( bank_list.size() );

        for( BankBean bb : bank_list )
        {
            m_bank_list.add( bb );
        }

    }

    /***
     *  Create a copy of our bankList ; however bank elements are shared so be careful.
     * @return New {@link List} but with shared {@link BankBean} objects.
     */
    public List<BankBean> getBankList()
    {
        // Clone the list structure ; however , elements are shared.
        List<BankBean>  bank_list = new ArrayList<BankBean>( m_bank_list.size() );

        for( BankBean bb : m_bank_list )
        {
            bank_list.add( bb );
        }

        return( bank_list );
    }

    private List<BankBean>  m_bank_list;

}
