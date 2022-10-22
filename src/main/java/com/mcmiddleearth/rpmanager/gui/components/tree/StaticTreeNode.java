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

package com.mcmiddleearth.rpmanager.gui.components.tree;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class StaticTreeNode implements TreeNode {
    private TreeNode parent;
    private String name;
    private File file;
    private final boolean directory;
    private final List<StaticTreeNode> children;

    public StaticTreeNode(TreeNode parent, String name, File file, boolean directory, List<StaticTreeNode> children) {
        this.parent = parent;
        this.name = name;
        this.file = file;
        this.directory = directory;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StaticTreeNode> getChildren() {
        return children;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public TreeNode getChildAt(int i) {
        return children.get(i);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public int getIndex(TreeNode treeNode) {
        return children.indexOf(treeNode);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children);
    }

    @Override
    public String toString() {
        return name;
    }
}
