/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.ctc;

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

    //----------------  INSTANCE VARS  ---------------

    protected BlocksModel   m_model;

}
