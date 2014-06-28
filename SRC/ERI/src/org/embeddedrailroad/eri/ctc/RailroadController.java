/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import java.util.List;
import org.embeddedrailroad.eri.block.interfaces.CtcBlockItem;

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
public class RailroadController {

    public RailroadController( RailroadModel model )
    {
        this.m_model = model;
    }

    public void updateBlocks( List<CtcBlockItem> changed_blocks, int reason_flags )
    {
        if( changed_blocks != null )
        {
            for( CtcBlockItem blk : changed_blocks )
            {
                m_model.updateBlock( blk, reason_flags );
            }
        }
    }

    //--------------------------  INSTANCE VARS  -------------------------

    protected RailroadModel   m_model;

}
