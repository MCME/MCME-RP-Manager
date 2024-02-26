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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public class StaticTreeNode implements TreeNode {
    private TreeNode parent;
    private String name;
    private File file;
    private final boolean directory;
    private final List<StaticTreeNode> children;
    private Git git;
    private NodeStatus status = NodeStatus.UNMODIFIED;
    private boolean visible = true;

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
        if (parent != null) {
            ((StaticTreeNode) parent).getChildren().sort(Comparator.comparing(StaticTreeNode::getName));
        };
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
        return filterVisible(children).get(i);
    }

    @Override
    public int getChildCount() {
        return filterVisible(children).size();
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
        return filterVisible(children).indexOf(treeNode);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return filterVisible(children).isEmpty();
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(filterVisible(children));
    }

    @Override
    public String toString() {
        return name;
    }

    public void addChild(StaticTreeNode child) {
        int index = (int) getChildren().stream().filter(c -> c.getName().compareTo(child.getName()) <= 0).count();
        getChildren().add(index, child);
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public void refreshGitStatus() throws GitAPIException {
        Status status = null;
        if (git != null && file.isDirectory()) {
            String path = resolvePath(file, (StaticTreeNode) parent);
            if (path == null) {
                status = git.status().call();
            } else {
                status = git.status().addPath(path).call();
            }
        }
        doRefreshGitStatus(this, status);
    }

    private static void doRefreshGitStatus(StaticTreeNode node, Status status) {
        String path = resolvePath(node.file, (StaticTreeNode) node.parent);
        if (status != null) {
            if (status.getAdded().contains(path)) {
                node.status = NodeStatus.ADDED;
            } else if (status.getModified().contains(path) || status.getChanged().contains(path)) {
                node.status = NodeStatus.MODIFIED;
            } else if (status.getUntracked().contains(path)) {
                node.status = NodeStatus.UNTRACKED;
            } else {
                node.status = NodeStatus.UNMODIFIED;
            }
            if (status.getUntrackedFolders().contains(path)) {
                setChildrenStatus(node, NodeStatus.UNTRACKED);
            } else if (node.children != null && !node.children.isEmpty()) {
                for (StaticTreeNode child : node.children) {
                    doRefreshGitStatus(child, status);
                }
            }
        } else {
            node.status = NodeStatus.UNMODIFIED;
        }
        if (node.file.isDirectory()) {
            node.status = NodeStatus.UNMODIFIED;
        }
    }

    private static void setChildrenStatus(StaticTreeNode node, NodeStatus status) {
        if (node.file.isDirectory()) {
            node.status = NodeStatus.UNMODIFIED;
        } else {
            node.status = status;
        }
        if (node.children != null && !node.children.isEmpty()) {
            for (StaticTreeNode child : node.children) {
                setChildrenStatus(child, status);
            }
        }
    }

    public NodeStatus getStatus() {
        return status;
    }

    public String getPath() {
        return resolvePath(file, (StaticTreeNode) parent);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void filter(String filter) {
        this.visible = shouldShow(filter);
        if (this.children != null) {
            this.children.forEach(c -> c.filter(filter));
        }
    }

    private boolean shouldShow(String filter) {
        return name.contains(filter) || children != null && children.stream().anyMatch(c -> c.shouldShow(filter));
    }

    private static String resolvePath(File file, StaticTreeNode parent) {
        if (parent == null) {
            return file.isDirectory() ? null : file.getName();
        }
        while (parent.getParent() != null) {
            parent = (StaticTreeNode) parent.getParent();
        }
        return parent.getFile().toPath().relativize(file.toPath()).toString();
    }

    private static List<StaticTreeNode> filterVisible(List<StaticTreeNode> list) {
        return list.stream().filter(StaticTreeNode::isVisible).toList();
    }

    public enum NodeStatus {
        ADDED,
        MODIFIED,
        UNTRACKED,
        UNMODIFIED
    }
}
