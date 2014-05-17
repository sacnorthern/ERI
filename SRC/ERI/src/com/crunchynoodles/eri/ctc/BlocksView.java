/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.ctc;

import com.crunchynoodles.eri.block.interfaces.CtcBlockItemListener;
import com.crunchynoodles.eri.block.interfaces.CtcSignalMastItemListener;
import com.crunchynoodles.eri.block.interfaces.CtcTurnoutItemListener;

/***
 *
 *  <pre><tt>
    BlocksModel m = new BlocksModel();
    BlocksView v = new BlocksView(m);
    BlocksController c = new BlocksController(m);
    MyFrame gui = new MyFrame(v, c);
</tt></pre>
 *
 * @author brian
 * @see http://stackoverflow.com/questions/20027887/mvc-java-how-does-a-controller-set-listeners-to-the-children-classes-of-a-view
 * @see http://www.oracle.com/technetwork/articles/javase/index-142890.html
 */
public class BlocksView
    implements CtcBlockItemListener, CtcSignalMastItemListener, CtcTurnoutItemListener
{

    public BlocksView( BlocksModel model )
    {
        this.m_model = model;
    }

    //-----------------------  BLOCK ITEM LISTENER  ----------------------

    @Override
    public void ctcBlockChanged( BlockItemChangedEvent evt ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    //----------------------  SIGNAL MAST LISTENER  ----------------------

    @Override
    public void ctcSignalChanged( SignalMastItemChangedEvent evt ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    //------------------------  TURNOUT LISTENER  ------------------------

    @Override
    public void ctcTurnoutChanged( TurnoutItemChangedEvent evt ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ctcTurnoutAskVeto( TurnoutItemChangedEvent evt ) {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    //--------------------------  INSTANCE VARS  -------------------------

    protected BlocksModel   m_model;

}
