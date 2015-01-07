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

package com.crunchynoodles.view.interfaces;

import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 *  Display of data for IOrderedListViewModel.  This class handles the GUI interactions
 *  and committing of data when the user accepts the changes.
 *
 *  Note: items are either in the list, or deleted and gone.  A deleted item is not saved.
 *
 * @author brian
 */
public abstract class AbstractOrderedListView
        extends JPanel
{

    /*** Use with {2code addVerbButton} to append instead of insert. */
    public static final int     INDEX_LAST = 99;

    // ----------------------------------------------------------------------------

    public IOrderedListViewModel getViewModel()
    {
        return m_model;
    }

    public void setViewModel( IOrderedListViewModel new_model )
    {
        m_model = new_model;
        // should probably fire a ChangedEvent here....
    }

    // ----------------------------------------------------------------------------

    abstract public void addVerbButton( int newButtonAtIndex, JButton verbButton );

    abstract public void doAcceptButton();

    abstract public void doCancelButton();

    // ----------------------------------------------------------------------------

    protected IOrderedListViewModel   m_model;

}
