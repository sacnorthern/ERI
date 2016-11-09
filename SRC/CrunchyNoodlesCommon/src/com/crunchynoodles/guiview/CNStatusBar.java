/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2015 in USA by Brian Witt , bwitt@value.net
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

package com.crunchynoodles.guiview;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 *   A panel that contains labels where status can be displayed.
 *
 *  @see <a href="http://stackoverflow.com/questions/3035880/how-can-i-create-a-bar-in-the-bottom-of-a-java-app-like-a-status-bar">stack overflow Status bar</a>
 * @author brian
 */
public class CNStatusBar extends JPanel
{
    private JLabel statusLabel;

    public CNStatusBar()
    {
        setBorder(new BevelBorder(BevelBorder.LOWERED));

        setLayout(new BorderLayout(2, 2));

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setForeground(Color.black);
        add(BorderLayout.CENTER, statusLabel);

        JLabel dummyLabel = new JLabel(" ");
        dummyLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        add(BorderLayout.EAST, dummyLabel);
    }

    public void setStatus(String status)
    {
        if (status.equals(""))
            statusLabel.setText("Ready");
        else
            statusLabel.setText(status);
    }

    public String getStatus()
    {
        return statusLabel.getText();
    }

}
