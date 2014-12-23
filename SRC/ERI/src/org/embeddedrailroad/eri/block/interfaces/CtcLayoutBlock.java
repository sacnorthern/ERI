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

import org.embeddedrailroad.eri.layoutio.LayoutIoModel;

/**
 *  Blocks are units of control, signaling and detection.
 *  A block is either occupied or not-occupied.
 *  Blocks own zero or more signals, based on zero or more detectors.
 *
 * @author brian
 */
public interface CtcLayoutBlock
{
    /*** @return Name of this block. */
    public String       getName();

    /*** @return Model-specific address "name" of where detector is located. */
    public String       getModelLocationName();

    /*** @return underlying model for this Detector.  */
    public LayoutIoModel<?> getModel();
}
