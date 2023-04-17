/*
 * Copyright (C) 2023 MCME
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

package com.mcmiddleearth.rpmanager.gui.components.tree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExpansionStateAwareTreeModel extends DefaultTreeModel {
    private JTree tree;

    public ExpansionStateAwareTreeModel(TreeNode root) {
        super(root);
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void reload() {
        TreePath selectionPath = tree.getSelectionPath();
        List<TreePath> nodes = getExpandedNodes();
        super.reload();
        restoreExpandedNodes(fixTreePaths(nodes));
        restoreSelection(fixTreePath(selectionPath));
    }

    @Override
    public void reload(TreeNode node) {
        TreePath selectionPath = tree.getSelectionPath();
        List<TreePath> nodes = getExpandedNodes();
        super.reload(node);
        restoreExpandedNodes(fixTreePaths(nodes));
        restoreSelection(fixTreePath(selectionPath));
    }

    private List<TreePath> fixTreePaths(List<TreePath> paths) {
        return paths.stream().map(this::fixTreePath).filter(Objects::nonNull).toList();
    }

    // Root of the tree is guaranteed to remain unchanged, child nodes might be replaced due to undo/redo actions and
    // reloads that follow them, so we need to update the path to use new nodes.
    private TreePath fixTreePath(TreePath path) {
        if (path == null) {
            return null;
        }
        if (path.getParentPath() != null && path.getLastPathComponent() instanceof StaticTreeNode node) {
            TreePath newParent = fixTreePath(path.getParentPath());
            StaticTreeNode newNode = newParent == null ?
                    null :
                    ((StaticTreeNode) newParent.getLastPathComponent()).getChildren().stream()
                            .filter(c -> c.getName().equals(node.getName())).findFirst().orElse(null);
            if (newNode != null) {
                return new TreePath(Stream.concat(Stream.of(newParent.getPath()), Stream.of(newNode)).toArray());
            } else {
                return null;
            }
        }
        return path;
    }

    private List<TreePath> getExpandedNodes() {
        int rows = tree.getRowCount();
        return IntStream.range(0, rows).filter(i -> tree.isExpanded(i)).mapToObj(i -> tree.getPathForRow(i)).toList();
    }

    private void restoreExpandedNodes(List<TreePath> nodes) {
        nodes.forEach(node -> tree.expandPath(node));
    }

    private void restoreSelection(TreePath selectionPath) {
        tree.setSelectionPath(selectionPath);
        if (selectionPath != null) {
            for (TreeSelectionListener listener : tree.getTreeSelectionListeners()) {
                listener.valueChanged(new TreeSelectionEvent(
                        tree, tree.getSelectionPath(), true, selectionPath, tree.getSelectionPath()));
            }
        }
    }
}
