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

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TreeGitAddAction extends Action {
    private final JTree tree;

    public TreeGitAddAction(JTree tree) {
        super("Add", "Add files to git");
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<StaticTreeNode> nodes = new LinkedList<>();
        Set<StaticTreeNode> nodesToRefresh = new HashSet<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : tree.getSelectionPaths()) {
                nodes.add((StaticTreeNode) path.getLastPathComponent());
            }
        }
        for (StaticTreeNode node : nodes) {
            try {
                node.getGit().add().addFilepattern(node.getPath()).call();
                StaticTreeNode nodeToRefresh = node;
                if (!nodeToRefresh.isDirectory()) {
                    nodeToRefresh = (StaticTreeNode) nodeToRefresh.getParent();
                }
                nodesToRefresh.add(nodeToRefresh);
            } catch (GitAPIException ex) {
                //TODO show error dialog
            }
        }
        for (StaticTreeNode node : nodesToRefresh) {
            try {
                node.refreshGitStatus();
            } catch (GitAPIException ex) {
                //TODO show error dialog
            }
        }
        tree.invalidate();
        tree.repaint();
    }
}
