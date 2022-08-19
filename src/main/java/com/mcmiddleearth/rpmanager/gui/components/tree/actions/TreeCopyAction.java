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

package com.mcmiddleearth.rpmanager.gui.components.tree.actions;

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.ClipboardFileWrapper;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeCopyAction extends Action {
    private final JTree tree;

    public TreeCopyAction(JTree tree) {
        super("Copy", null, "Copy files to clipboard", KeyEvent.VK_C, KeyEvent.VK_C);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        List<StaticTreeNode> nodes = new LinkedList<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : tree.getSelectionPaths()) {
                nodes.add((StaticTreeNode) path.getLastPathComponent());
            }
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new ClipboardFileWrapper(nodes.stream().map(StaticTreeNode::getFile).collect(Collectors.toList())),
                (clipboard, transferable) -> {});
    }
}
