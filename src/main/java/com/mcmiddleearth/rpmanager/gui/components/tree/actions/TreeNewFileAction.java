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
import com.mcmiddleearth.rpmanager.gui.modals.NewFileModal;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TreeNewFileAction extends Action {
    private final JTree tree;

    public TreeNewFileAction(JTree tree) {
        super("File", "Create new file");
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StaticTreeNode node = (StaticTreeNode) tree.getSelectionPath().getLastPathComponent();
        if (!node.isDirectory()) {
            node = (StaticTreeNode) node.getParent();
        }
        new NewFileModal(MainWindow.getInstance(), tree, node);
    }
}
