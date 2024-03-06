/*
 * Copyright (C) 2022 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Actions;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;

import javax.swing.*;
import java.awt.*;

public class EmptyProjectPane extends JPanel {
    public EmptyProjectPane() {
        setLayout(new BorderLayout());

        VerticalBox internal = new VerticalBox(GridBagConstraints.NONE);
        internal.add(Box.createVerticalGlue());
        internal.add(new JMenuItem(Actions.NEW_PROJECT));
        internal.add(new JMenuItem(Actions.OPEN_PROJECT));
        internal.add(Box.createVerticalStrut(50));
        JLabel label = new JLabel("Recent projects:");
        label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD, 24.0f));
        internal.add(label);
        VerticalBox openRecentBox = new VerticalBox(GridBagConstraints.NONE);
        for (Component c : MainWindow.getInstance().getOpenRecentMenu().getMenuComponents()) {
            openRecentBox.add(c);
        }
        internal.add(new FastScrollPane(openRecentBox));
        add(internal, BorderLayout.CENTER);
    }
}
