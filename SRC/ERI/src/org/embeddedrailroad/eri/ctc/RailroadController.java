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
