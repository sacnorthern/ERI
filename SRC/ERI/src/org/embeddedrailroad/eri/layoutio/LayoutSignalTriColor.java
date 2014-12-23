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

package org.embeddedrailroad.eri.layoutio;

/**
 *
 * @author brian
 */
public class LayoutSignalTriColor // extends LayoutSignal
{
    public final static int     CODE_DARK = 0x0;
    public final static int     CODE_RED_ON = 0x1;
    public final static int     CODE_YELLOW_ON = 0x2;
    public final static int     CODE_GREEN_ON = 0x3;

    public LayoutSignalTriColor()
    {

    }

}
