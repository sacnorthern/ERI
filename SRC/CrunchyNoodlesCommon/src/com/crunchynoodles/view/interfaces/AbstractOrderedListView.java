
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
