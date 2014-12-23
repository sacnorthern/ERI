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
import org.embeddedrailroad.eri.block.interfaces.CtcTurnoutItemListener;
import org.embeddedrailroad.eri.block.interfaces.CtcTurnoutItem;
import org.embeddedrailroad.eri.block.interfaces.CtcBlockItemListener;
import org.embeddedrailroad.eri.block.interfaces.CtcSignalMastItemListener;
import org.embeddedrailroad.eri.block.interfaces.CtcBlockItem;
import org.embeddedrailroad.eri.ctc.exceptions.UnknownCtcBlockException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Set;
import javax.swing.event.EventListenerList;


/**
 *
 *  <pre><tt>
    RailroadModel m = new RailroadModel();
    RailroadView v = new RailroadView(m);
    RailroadController c = new RailroadController(m);
    MyFrame gui = new MyFrame(v, c);
</tt></pre>
 *
 * @author brian
 */
public class RailroadModel {

    public RailroadModel()
    {
        this.m_baseline_model = null;

        m_blocks = new HashMap<>();
        m_block_change_reasons = new HashMap<>();

        m_signals = new HashMap<>();

        m_turnouts = new HashMap<>();
        m_turnout_veto_listeners = new ArrayList<>();

        m_change_listeners = new EventListenerList();

    }

    protected RailroadModel( RailroadModel baseline )
    {
        this();
        this.m_baseline_model = baseline;   // link back to baseline.

        //  Listeners are not copied.  If changes occur inside transaction, they
        //  are not notified.  Once the transaction commits, then the baseline
        //  model will fire change events.
    }

    //-------------------------  TRANSACTION  -------------------------

    /***
     *  Copy over the event listeners into this object.
     *  Element count must be even, [n] is class and [n+1] is listener object.
     * @param ell collection of listeners to copy
     */
    // Transactions keep their own list of listeners, if any.
    // When the trx commits, the baseline's listeners are notified.
    // That way if trx aborts, no listeners need to undo their reaction.
    //      brian witt, May 2014.
    //
//    private void _copyListeners( EventListenerList ell )
//    {
//        Object listeners[] = ell.getListenerList();
//
//        for (int j = listeners.length-2; j>=0; j-=2) {
//            try {
//                Class <EventListener>  which = (Class <EventListener>) listeners[j];
//                EventListener  it = (EventListener) listeners[j+1];
//                this.m_change_listeners.add( which, it );
//            } catch( Throwable e )
//            {
//                // do nothing, copy is aborted.
//                // but this should not happen!
//            }
//        }
//    }

    /***
     *  Setting lists to null will surely cause an exception if caller tries to use them!
     */
    protected void _wipeFields()
    {
        this.m_baseline_model = null;

        this.m_blocks = null;
        this.m_block_change_reasons = null;

        this.m_signals = null;
        this.m_turnouts = null;
        this.m_turnout_veto_listeners = null;
        this.m_change_listeners = null;
    }

    /***
     *  Create a model proxy.  Do all changes on proxy and then either {@code commitWith()}
     *  or {@code Abort()} them when done.
     *  A later {@code commitWith()} will fire changed events from within the master model.
     *
     * @return New Model to play with, with empty listener lists.
     */
    public RailroadModel startTransaction()
    {
        RailroadModel  trx = new RailroadModel( this );
        //! No listeners cuz trx might be aborted, so wait until commited. trx._copyListeners( this.m_change_listeners );
        return( trx );
    }

    /***
     *  Commit the changes made on this proxy model into the baseline model.
     *  Afterwards, this object is invalid.
     *
     * @exception IllegalStateException if not a transaction model.
     */
    public void commitWith()
    {
        if( m_baseline_model == null )
        {
            throw new IllegalStateException("cannot commitWith() upon base model!");
        }

        // TODO: commit to baseline.

        //  Now we're done and 'this' is invalid.
        //  Setting lists to null will surely cause an exception if caller tries to use them!
        _wipeFields();
    }

    public void abort()
    {
        //  Now we're done and 'this' is invalid.
        //  Setting lists to null will surely cause an exception if caller tries to use them!
        _wipeFields();
    }

    //---------------------------  BLOCKS  ----------------------------

    /***
     *  Insert one block into model.
     *  Will fire block-changed event when transaction commits, or immediately if called
     *  on the baseline model.
     */
    public void     addBlock( CtcBlockItem new_block )
    {
        this.m_blocks.put(  new_block.getName(), new_block );

        if( this.m_baseline_model == null )
            this.fireBlockOccupancyChanged( new CtcBlockItem[] { new_block }, BlockItemChangedEvent.CUZ_ADD_TO_MODEL );
    }

    /***
     *  Add a list of blocks.  After the batch is in, the block-changed event is fired.
     *  Used during initialization to populate the model.
     * @param new_block_list array of blocks to add, could be null or empty.
     */
    public void     addBlocks( CtcBlockItem[] new_block_list )
    {
        m_lock.writeLock().lock();
        try
        {
            for( CtcBlockItem blk : new_block_list )
            {
                this.m_blocks.put( blk.getName(), blk );
            }
        } finally
        {
            m_lock.writeLock().unlock();
        }

        //  If stuff added to baseline, then fire the event!
        if( new_block_list != null && new_block_list.length > 0 && this.m_baseline_model != null )
            this.fireBlockOccupancyChanged( new_block_list, BlockItemChangedEvent.CUZ_ADD_TO_MODEL );
    }

    /***
     *  Note a change-reason for some block (reference provided),
     *  firing change-event when trx completes or immediately if model is baseline.
     *  Clients are given references that they are expected to change as needed.
     *  After modifying ( we don't know <em>when</em> the changes occurred ),
     *  client calls here with a change-reason code.
     *  The name cannot be changed here; otherwise {link UnknownCtcBlockException} is thrown.
     *
     * @param changed_block referenced to block already in model.
     * @param reason_flags See {@link BlockItemChangedEvent} {@code CUZ_ZZZ} codes
     */
    public void     updateBlock( CtcBlockItem changed_block, int reason_flags )
    {
        if( changed_block == null )
            return;

        if( getBlock( changed_block.getName() ) == null )
        {
            throw new UnknownCtcBlockException( "Block " + changed_block.getName() + "unknown" );
        }

        Integer  now_value = m_block_change_reasons.get( changed_block.getName() );
        if( now_value != null )
        {
            now_value = now_value.intValue() | reason_flags;
        }
        else
        {
            now_value = new Integer( reason_flags );
        }
        m_block_change_reasons.put( changed_block.getName(), now_value );

    }

    /***
     *  Makes a shallow-list, i.e. every item is a reference into model.
     *  Returns state from base model, overlaid with changes in the transaction.
     *
     * @return List of all blocks with references into model.
     */
    public ArrayList<CtcBlockItem> getBlockList()
    {
        ArrayList<CtcBlockItem>  list = null;

        if( m_baseline_model != null )
        {
            list = m_baseline_model.getBlockList();
        }

        m_lock.readLock().lock();
        try
        {
            if( list != null )
            {
                //  get signal names we want to insert, along with an iterator that
                //  allows us to modify the underlying list.
                Set<String> overrides = m_blocks.keySet();
                ListIterator<CtcBlockItem>  has_list = list.listIterator();
                CtcBlockItem    c;

                //  Remove those that will be overwritten.
                while( has_list.hasNext() )
                {
                    c = has_list.next();
                    if( overrides.contains( c.getName() ) )
                    {
                        has_list.remove();
                    }
                }

                //  Add those changed in this transaction.
                list.addAll( m_blocks.values() );
            }
            else
            {
                //  NULL means we are the baseline.
                list = new ArrayList<>( m_blocks.values() );
            }
        }
        finally
        {
            m_lock.readLock().unlock();
        }
        return list;
    }

    public CtcBlockItem getBlock( String block_name )
    {
        CtcBlockItem   blk = null;

        if( m_blocks.containsKey( block_name ) )
        {
            blk = m_blocks.get( block_name );
        }
        else if( m_baseline_model != null )
        {
            blk = m_baseline_model.getBlock( block_name );
        }

        return blk;
    }

    //---------------------------  SIGNALS  ---------------------------

    /***
     *  Makes a shallow-list, i.e. every item is a reference into model.
     *  Returns state from base model, overlaid with changes in the transaction.
     *
     * @return List of all signals with references into model.
     */
    public ArrayList<CtcSignalMastItem> getSignalList()
    {
        ArrayList<CtcSignalMastItem>  list = null;

        if( m_baseline_model != null )
        {
            list = m_baseline_model.getSignalList();
        }

        m_lock.readLock().lock();
        try
        {
            if( list != null )
            {
                //  get signal names we want to insert, along with an iterator that
                //  allows us to modify the underlying list.
                Set<String> overrides = m_signals.keySet();
                ListIterator<CtcSignalMastItem>  has_list = list.listIterator();
                CtcSignalMastItem   c = null;

                //  Remove those that will be overwritten.
                while( has_list.hasNext() )
                {
                    c = has_list.next();
                    if( overrides.contains( c.getName() ) )
                    {
                        has_list.remove();
                    }
                }

                //  add those changed in this transaction.
                list.addAll( m_signals.values() );
            }
            else
            {
                //  NULL means we are the baseline.
                list = new ArrayList<>( m_signals.values() );
            }
        }
        finally
        {
            m_lock.readLock().unlock();
        }
        return list;
    }

    /***
     *  Lookup a named signal and return a reference. I.e. changing it changes the model.
     * @param sig_name name of signal.
     * @return {@code null} if not found.
     */
    public CtcSignalMastItem getSignal( String sig_name )
    {
        CtcSignalMastItem   sm = null;

        if( m_signals.containsKey( sig_name ) )
        {
            sm = m_signals.get( sig_name );
        }
        else if( m_baseline_model != null )
        {
            sm = m_baseline_model.getSignal( sig_name );
        }

        return sm;
    }

    //-------------------------  TURNOUTS  ---------------------------

    /***
     *  Makes a shallow-list, i.e. every item is a reference into model.
     *  Returns state from base model, overlaid with changes in the transaction.
     *
     * @return List of all turnouts with references into model.
     */
    public ArrayList<CtcTurnoutItem> getTurnoutList()
    {
        ArrayList<CtcTurnoutItem>  list = null;

        if( m_baseline_model != null )
        {
            list = m_baseline_model.getTurnoutList();
        }

        m_lock.readLock().lock();
        try
        {
            if( list != null )
            {
                //  get signal names we want to insert, along with an iterator that
                //  allows us to modify the underlying list.
                Set<String> overrides = m_turnouts.keySet();
                ListIterator<CtcTurnoutItem>  has_list = list.listIterator();
                CtcTurnoutItem   c = null;

                //  Remove those that will be overwritten.
                while( has_list.hasNext() )
                {
                    c = has_list.next();
                    if( overrides.contains( c.getName() ) )
                    {
                        has_list.remove();
                    }
                }

                //  add those changed in this transaction.
                list.addAll( m_turnouts.values() );
            }
            else
            {
                //  NULL means we are the baseline.
                list = new ArrayList<>( m_turnouts.values() );
            }
        }
        finally
        {
            m_lock.readLock().unlock();
        }
        return list;
    }

    /***
     *  Search for a turnout with a specific name.
     *  Returns reference within the transaction, or the base that we run on.
     * @param turnout_name sting name of turnout, cannot be {@code null}.
     * @return reference, or {@code null} if not found.
     */
    public CtcTurnoutItem getTurnout( String turnout_name )
    {
        CtcTurnoutItem   turn = null;

        if( m_turnouts.containsKey( turnout_name ) )
        {
            turn = m_turnouts.get( turnout_name );
        }
        else if( m_baseline_model != null )
        {
            turn = m_baseline_model.getTurnout( turnout_name );
        }

        return turn;
    }

    //--------------------------  LISTENERS  --------------------------

    /***
     *  Add a listener for block changed events,
     *  e.g. {@code BlockItemChangedEvent.CUZ_OCCUPANCY} event.
     * @param me object to notify.
     */
    public void addBlockOccupancyListener( CtcBlockItemListener me )
    {
        m_change_listeners.add( CtcBlockItemListener.class, me );
    }

    /***
     *  Remove a listener of block events.
     * @param me object to stop notifying.
     */
    public void removeBlockOccupancyListener( CtcBlockItemListener me )
    {
        m_change_listeners.remove( CtcBlockItemListener.class, me );
    }

    /***
     *  Inform all {@code CtcBlockItemListener}'s that a block has changed for some reason or reasons.
     * @param change_list list of blocks, all with same change reason.
     * @param reason_flags set of flags, same for all blocks in array.
     *  @see http://stackoverflow.com/questions/3240472/when-to-use-eventlistenerlist-instead-of-a-general-collection-of-listeners
     */
    protected void fireBlockOccupancyChanged( CtcBlockItem[] change_list, int reason_flags )
    {
        CtcBlockItemListener[]  ll = m_change_listeners.getListeners(CtcBlockItemListener.class);

        for( CtcBlockItem blk : change_list )
        {
            BlockItemChangedEvent  evt = new BlockItemChangedEvent( blk, reason_flags );

            for( CtcBlockItemListener wants : ll )
            {
                wants.ctcBlockChanged( evt );
            }
        }
    }

    /***
     *  Add a listener for signal-mast changed events,
     *  e.g. {@code SignalMastItemChangedEvent.CUZ_INDICATION_CHANGED} event.
     * @param me object to notify.
     */
    public void addSignalChangedListener( CtcSignalMastItemListener me )
    {
        m_change_listeners.add( CtcSignalMastItemListener.class, me );
    }

    /***
     *  Remove a listener of signal-mast events.
     * @param me object to stop notifying.
     */
    public void removeSignalChangedListener( CtcSignalMastItemListener me )
    {
        m_change_listeners.remove( CtcSignalMastItemListener.class, me );
    }

    /***
     *  Add a listener for turnout changed events,
     *  e.g. {@code TurnoutItemChangedEvent.CUZ_NOW_INDETERMINATE} event.
     * @param me object to notify.
     */
    public synchronized void addTurnoutChangeRequestListener( CtcTurnoutItemListener me )
    {
        m_turnout_veto_listeners.add( me );
    }

    /***
     *  Remove a listener of turnout events.
     * @param me object to stop notifying.
     */
    public synchronized void removeTurnoutChangeRequestListener( CtcTurnoutItemListener me )
    {
        m_turnout_veto_listeners.remove( me );
    }

    /***
     *  Ask all turnout listeners if they like or dislike a turnout changing.
     *  If dislike, then a veto is a {@code true} return.
     * @param changing_list
     * @return {@code true} if change vetoed and must not occur.
     */
    protected boolean fireTurnoutVetoOpportunity( CtcTurnoutItem[] changing_list )
    {
        CtcTurnoutItemListener[]  ll = (CtcTurnoutItemListener[]) m_turnout_veto_listeners.toArray();
        for( CtcTurnoutItem  turn : changing_list )
        {
            TurnoutItemChangedEvent  evt = new TurnoutItemChangedEvent( turn, TurnoutItemChangedEvent.CUZ_CHECKING_VETO );
            for( CtcTurnoutItemListener wants : ll )
            {
                if( wants.ctcTurnoutAskVeto( evt ) )
                    return true;
            }
        }

        // No listener raised veto, so allow change.
        return false;
    }

    protected void fireTurnoutChanged( CtcTurnoutItem[] change_list, int reason_flags )
    {
        CtcTurnoutItemListener[]  ll = (CtcTurnoutItemListener[]) m_turnout_veto_listeners.toArray();
        for( CtcTurnoutItem  turn : change_list )
        {
            TurnoutItemChangedEvent  evt = new TurnoutItemChangedEvent( turn, reason_flags );
            for( CtcTurnoutItemListener wants : ll )
            {
                wants.ctcTurnoutChanged( evt );
            }
        }
    }

    public void addTurnoutChangedListener( CtcTurnoutItemListener me )
    {
        m_change_listeners.add( CtcTurnoutItemListener.class, me );
    }

    public void removeTurnoutChangedListener( CtcTurnoutItemListener me )
    {
        m_change_listeners.remove( CtcTurnoutItemListener.class, me );
    }

    //--------------------------  INSTANCE VARS  -------------------------

    /***
     *  Data access READER/WRITER lock.
     *  @see https://www.obsidianscheduler.com/blog/java-concurrency-part-2-reentrant-locks/
     */
    private ReadWriteLock   m_lock = new ReentrantReadWriteLock();


    /*** For transaction, link back to baseline Blocks Model object. */
    protected RailroadModel   m_baseline_model;

    // ArrayList<>  : Note that this implementation is not synchronized

    protected HashMap<String, CtcBlockItem>   m_blocks;

    private HashMap< String, Integer >  m_block_change_reasons;

    protected HashMap<String, CtcSignalMastItem>  m_signals;

    protected HashMap<String, CtcTurnoutItem> m_turnouts;

    //-------------------------  LISTENER SUPPORT  -----------------------

    private EventListenerList   m_change_listeners;

    private ArrayList< CtcTurnoutItemListener >   m_turnout_veto_listeners;

}
