package com.crunchynoodles.view.interfaces;

import java.util.List;
import com.crunchynoodles.util.*;

/**
 *  Receptacle of data for OrderedListView.  The "ViewModel" is the interface to an
 *  ordered-list of String data.
 *
 *  Note: items are either in the list, or deleted and gone.  A deleted item is not saved.
 *
 * @author brian
 */
public interface IOrderedListViewModel {

    //  QUALIFIERS.

    /*** Return true if duplicates are allowed, otherwise View will prevent them. */
    public boolean AreDuplicatesAllowed();

    /*** Return title of data.  This is presented to the user. */
    public String GetTitle();

    /*** Return sub-title of data.  This is presented to the user. */
    public String GetSubTitle();

    //  ACCESSORS.

    /***
     *  Return the list of items.  The key 'String' is presented to the user.
     *  The value 'Object' is whatever data the back-end needs.
     */
    public List< KeyValue<String, Object> >  GetItems();

    public IOrderedListViewModel MakeScratchCopy();

    //  MUTATORS.

    /***
     *  Add a new key in the list.
     * @param where - after item index, -1 for first in list, 0 for right after
     *              first item
     */
    public String AddNewItem( int where, String key );

    public String RemoveItem( String key );

    public String RemoveItem( int where );

    /***
     *  Use the scratch copy as the primary, replacing the previous data for this ViewModel.
     *  After this call {@code scratch} is no longer valid.
     * @param scratch
     * @return null on success, non-null otherwise.
     */
    public String UpdateFromScratchCopy( IOrderedListViewModel scratch );

    /***
     *  Dispose of the scratch "copy".
     *  After this call {@code scratch} is no longer valid.
     * @param scratch
     * @return null on success, non-null otherwise.
     */
    public String DisposeOfScratchCopy( IOrderedListViewModel scratch );

}
