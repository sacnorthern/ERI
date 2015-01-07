/***  Java Commons and Niceties Library from CrunchyNoodles.com
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

package com.crunchynoodles.view;

import com.crunchynoodles.util.StringUtils;
import com.crunchynoodles.view.interfaces.AbstractOrderedListView;
import com.crunchynoodles.view.interfaces.IOrderedListViewModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author brian
 */
public class OrderedListView extends AbstractOrderedListView
{
    public static final String MSG_ACCEPT = "OK";
    public static final String MSG_REJECT = "Cancel";

    // ----------------------------------------------------------------------------

    public OrderedListView()
    {
        _initComponent();
    }

    // ----------------------------------------------------------------------------

    @Override
    public void setViewModel( IOrderedListViewModel new_model )
    {
        super.setViewModel( new_model );

        //  1.  Extract title and sub-title.
        Ltitle.setText( m_model.GetTitle() );

        m_title_subtitle.removeAll();
        m_title_subtitle.add( Ltitle );

        String  subttl = m_model.GetSubTitle();
        if( ! StringUtils.emptyOrNull( subttl ) )
        {
            Lsubtitle.setText( subttl );
            m_title_subtitle.add( Lsubtitle );
        }

        //  2.  Populate the list of names.

        //  n.  Invalidate layout...
        m_title_subtitle.invalidate();
    }



    @Override
    public void addVerbButton( int newButtonAtIndex, JButton verbButton )
    {

    }

    @Override
    public void doAcceptButton()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doCancelButton()
    {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }


    // ----------------------------------------------------------------------------

    /***
     *  Build up the GUI widgets of the OrderedListView.
     *  To the basic {@code BorderLayout}, NORTH gets a Vertical box for the title and
     *  subtitle.
     *  EAST gets a vertical stack of buttons with [Add...] on the top and [Remove] on the bottom.
     *  SOUTH gets a Horizontal box that contains the [
     */
    private void _initComponent()
    {
        this.setLayout( new BorderLayout() );

        //***  NORTH  ***//
        m_title_subtitle = new Box( BoxLayout.Y_AXIS );
        Ltitle = new JLabel( "(title)" );
        Lsubtitle = new JLabel();

        //  title and subtitle are added when the model changes.

        //***  CENTER  ***//
        m_list = new JList();


        //***  EAST  ***//
        m_verb_buttons = new Box( BoxLayout.Y_AXIS );

        m_accept_reject_buttons = new Box( BoxLayout.X_AXIS );
        Bok = new JButton( new AbstractAction( MSG_ACCEPT) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                doAcceptButton();
            }
        });
        Breject = new JButton( new AbstractAction( MSG_REJECT ) {
            @Override
            public void actionPerformed( ActionEvent e ) {
                doCancelButton();
            }
        });

        //***  SOUTH  ***//
        m_accept_reject_buttons.add( Bok );
        m_accept_reject_buttons.add( Breject );
    }


    // ----------------------------------------------------------------------------

    Box     m_title_subtitle;
    JLabel      Ltitle;
    JLabel      Lsubtitle;

    JList   m_list;

    Box     m_verb_buttons;
    JButton     Bok;
    JButton     Breject;

    Box     m_accept_reject_buttons;
}
