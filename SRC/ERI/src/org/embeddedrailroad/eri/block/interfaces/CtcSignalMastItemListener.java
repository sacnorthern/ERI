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

package org.embeddedrailroad.eri.block.interfaces;

import java.util.EventListener;
import org.embeddedrailroad.eri.ctc.SignalMastItemChangedEvent;

/**
 *
 * @author brian
 */
public interface CtcSignalMastItemListener extends EventListener {

    /*** Called when something about a signal-mast changes. */
    public void ctcSignalChanged( SignalMastItemChangedEvent evt );
}
