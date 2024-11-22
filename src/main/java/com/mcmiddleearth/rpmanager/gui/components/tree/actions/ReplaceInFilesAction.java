/*
 * Copyright (C) 2024 MCME
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

package com.mcmiddleearth.rpmanager.gui.components.tree.actions;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.modals.ReplaceInFilesModal;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

public class ReplaceInFilesAction extends Action {
    private final JTree tree;

    public ReplaceInFilesAction(JTree tree) {
        super("Replace in files...", null, "Replace text in all selected files",
                KeyEvent.VK_E, null);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<StaticTreeNode> nodes = new LinkedList<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : tree.getSelectionPaths()) {
                StaticTreeNode node = (StaticTreeNode) path.getLastPathComponent();
                if (!node.isDirectory()) {
                    nodes.add(node);
                }
            }
        }
        new ReplaceInFilesModal(MainWindow.getInstance(), nodes);
    }
}
