/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.ctc;

import java.util.ArrayList;
import com.crunchynoodles.eri.block.interfaces.CtcBlockItem;

/**
 *
 *  <pre><tt>
    BlocksModel m = new BlocksModel();
    BlocksView v = new BlocksView(m);
    BlocksController c = new BlocksController(m);
    MyFrame gui = new MyFrame(v, c);
</tt></pre>
 *
 * @author brian
 */
public class BlocksController {

    public BlocksController( BlocksModel model )
    {
        this.m_model = model;
    }

    public void updateBlocks( ArrayList<CtcBlockItem> changed_blocks, int reason_flags )
    {
        for( CtcBlockItem blk : changed_blocks )
        {
            m_model.updateBlock( blk, reason_flags );
        }
    }

    //--------------------------  INSTANCE VARS  -------------------------

    protected BlocksModel   m_model;

}
