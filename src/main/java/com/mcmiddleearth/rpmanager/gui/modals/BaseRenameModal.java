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

import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;

public abstract class BaseRenameModal extends JDialog {
    private final JTree tree;

    protected BaseRenameModal(Frame parent, String title, JTree tree) {
        super(parent, title, true);
        this.tree = tree;
    }

    protected void renameNode(StaticTreeNode node, String newName) {
        node.setName(newName);
        File destination = new File(node.getFile().getParentFile(), newName);
        node.getFile().renameTo(destination);
        node.setFile(destination);
        ((DefaultTreeModel) tree.getModel()).reload(node.getParent());
    }
}
