package com.mcmiddleearth.rpmanager.gui.components.tree;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class StaticTreeNode implements TreeNode {
    private TreeNode parent;
    private final String name;
    private final List<StaticTreeNode> children;

    public StaticTreeNode(TreeNode parent, String name, List<StaticTreeNode> children) {
        this.parent = parent;
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<StaticTreeNode> getChildren() {
        return children;
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
