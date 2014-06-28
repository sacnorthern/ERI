/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import org.embeddedrailroad.eri.block.interfaces.CtcBlockItemListener;
import org.embeddedrailroad.eri.block.interfaces.CtcSignalMastItemListener;
import org.embeddedrailroad.eri.block.interfaces.CtcTurnoutItemListener;

/***
 *
 *  <pre><tt>
    RailroadModel m = new RailroadModel();
    RailroadView v = new RailroadView(m);
    RailroadController c = new RailroadController(m);
    MyFrame gui = new MyFrame(v, c);
</tt></pre>
 *
 * @author brian
 * @see http://stackoverflow.com/questions/20027887/mvc-java-how-does-a-controller-set-listeners-to-the-children-classes-of-a-view
 * @see http://www.oracle.com/technetwork/articles/javase/index-142890.html
 */
public class RailroadView
    implements CtcBlockItemListener, CtcSignalMastItemListener, CtcTurnoutItemListener
{

    public RailroadView( RailroadModel model )
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

    protected RailroadModel   m_model;

}
