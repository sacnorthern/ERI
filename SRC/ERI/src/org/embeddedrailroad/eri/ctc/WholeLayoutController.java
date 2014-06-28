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
    WholeLayoutModel m = new WholeLayoutModel();
    BlocksView v = new BlocksView(m);
    WholeLayoutController c = new WholeLayoutController(m);
    MyFrame gui = new MyFrame(v, c);
</tt></pre>
 *
 * @author brian
 */
public class WholeLayoutController {

    public WholeLayoutController( WholeLayoutModel model )
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

    protected WholeLayoutModel   m_model;

}
