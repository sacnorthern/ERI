/***  This file is dedicated to the public domain, 2014, 2016 Brian Witt in USA.  ***/

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
public class RailroadControllerTest {

    public RailroadControllerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of updateBlocks method, of class RailroadController.
     */
    @Test
    public void testUpdateBlocks()
    {
        System.out.println( "updateBlocks" );
        int reason_flags = 0;

        RailroadModel m = new RailroadModel();
        RailroadView v = new RailroadView(m);
        RailroadController c = new RailroadController(m);

        //  1.a  With no changed_blocks, don't throw exceptions.
        List<CtcBlockItem> changed_blocks = null;
        c.updateBlocks( changed_blocks, reason_flags );

        //  1.b  With empty changed_blocks, don't throw exceptions.
        changed_blocks = new ArrayList<CtcBlockItem>();
        c.updateBlocks( changed_blocks, reason_flags );

        //  2.  Put in some blocks.
    }
}
