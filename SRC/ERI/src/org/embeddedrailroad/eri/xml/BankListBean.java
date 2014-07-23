/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.xml;

import com.crunchynoodles.util.XmlEntityBean;
import java.util.ArrayList;
import java.util.List;


/**
 *  {@code &gt; !ELEMENT bankList (bank*) &gt;}
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
        m_bank_list = new ArrayList<BankBean>( 1 );
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

    public List<BankBean> getBankList()
    {
        List<BankBean>  bank_list = new ArrayList<BankBean>( m_bank_list.size() );

        for( BankBean bb : m_bank_list )
        {
            bank_list.add( bb );
        }

        return( bank_list );
    }

    private List<BankBean>  m_bank_list;

}
