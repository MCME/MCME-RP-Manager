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

package com.mcmiddleearth.rpmanager.gui.modals;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class MassRenameModal extends JDialog {
    public MassRenameModal(Frame parent, JTree tree, List<StaticTreeNode> nodes) {
        super(parent, "Rename files", true);

        setLayout(new BorderLayout());
        JLabel label = new JLabel("How would you like to rename your files?");
        add(label, BorderLayout.CENTER);
        JButton replace = new JButton(new Action("Replace part", "Replace part of name in all selected files") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new MassRenameReplaceModal(parent, tree, nodes);
                MassRenameModal.this.close();
            }
        });
        JButton pattern = new JButton(new Action("Set new names",
                "Set a pattern that can be used to name all selected files") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new MassRenamePatternModal(parent, tree, nodes);
                MassRenameModal.this.close();
            }
        });
        JButton cancel = new JButton(new Action("Cancel", "Cancel renaming files") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MassRenameModal.this.close();
            }
        });
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonsPanel.add(replace);
        buttonsPanel.add(pattern);
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        setLocation(MainWindow.getInstance().getMousePosition());
        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
