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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.stream.IntStream;

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
        List<TreePath> nodes = getExpandedNodes();
        super.reload();
        restoreExpandedNodes(nodes);
    }

    @Override
    public void reload(TreeNode node) {
        List<TreePath> nodes = getExpandedNodes();
        super.reload(node);
        restoreExpandedNodes(nodes);
    }

    private List<TreePath> getExpandedNodes() {
        int rows = tree.getRowCount();
        return IntStream.range(0, rows).filter(i -> tree.isExpanded(i)).mapToObj(i -> tree.getPathForRow(i)).toList();
    }

    private void restoreExpandedNodes(List<TreePath> nodes) {
        nodes.forEach(node -> tree.expandPath(node));
    }
}
