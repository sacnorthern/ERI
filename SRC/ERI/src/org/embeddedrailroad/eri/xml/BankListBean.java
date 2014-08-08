/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.ArrayList;
import java.util.List;


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