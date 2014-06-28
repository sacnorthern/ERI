/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

import java.util.ArrayList;
import java.util.List;
import org.embeddedrailroad.eri.block.interfaces.CtcBlockItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author brian
 */
public class BlocksControllerTest {

    public BlocksControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of updateBlocks method, of class BlocksController.
     */
    @Test
    public void testUpdateBlocks() {
        System.out.println( "updateBlocks" );
        int reason_flags = 0;

        BlocksModel m = new BlocksModel();
        BlocksView v = new BlocksView(m);
        BlocksController c = new BlocksController(m);

        //  1.a  With no changed_blocks, don't throw exceptions.
        List<CtcBlockItem> changed_blocks = null;
        c.updateBlocks( changed_blocks, reason_flags );

        //  1.a  With empty changed_blocks, don't throw exceptions.
        changed_blocks = new ArrayList<CtcBlockItem>();
        c.updateBlocks( changed_blocks, reason_flags );

        //  2.  Put in some blocks.
    }
}