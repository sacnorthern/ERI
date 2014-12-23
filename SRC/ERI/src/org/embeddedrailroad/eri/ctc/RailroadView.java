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
