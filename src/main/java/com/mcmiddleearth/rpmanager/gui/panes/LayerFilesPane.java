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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.gui.components.tree.ExpansionStateAwareTreeModel;
import com.mcmiddleearth.rpmanager.gui.components.tree.JarTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.ResourcePackTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.components.tree.actions.*;
import com.mcmiddleearth.rpmanager.gui.listeners.LayerTreeSelectionListener;
import com.mcmiddleearth.rpmanager.model.project.Layer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LayerFilesPane extends JPanel {
    private final Layer layer;
    private final JTree tree;
    private boolean eventsEnabled = true;

    public LayerFilesPane(Layer layer) throws IOException {
        this.layer = layer;

        setLayout(new BorderLayout());
        add(new JLabel(layer.getName()), BorderLayout.PAGE_START);
        add(this.tree = createTree(layer.getFile()), BorderLayout.CENTER);
    }

    private static JTree createTree(File file) throws IOException {
        ExpansionStateAwareTreeModel model = new ExpansionStateAwareTreeModel(createRootNode(file));
        boolean editable = !file.getName().endsWith(".jar");
        JTree tree = new JTree(model);
        model.setTree(tree);
        tree.setComponentPopupMenu(createPopupMenu(tree, editable));
        return tree;
    }

    private static StaticTreeNode createRootNode(File file) throws IOException {
        return file.getName().endsWith(".jar") ?
                JarTreeFactory.createRootNode(file) : ResourcePackTreeFactory.createRootNode(file);
    }

    private static JPopupMenu createPopupMenu(JTree tree, boolean editable) {
        Action copyAction = new TreeCopyAction(tree);
        Action pasteAction = new TreePasteAction(tree);
        pasteAction.setEnabled(editable);
        Action deleteAction = new TreeDeleteAction(tree);
        deleteAction.setEnabled(editable);
        Action renameAction = new TreeRenameAction(tree);
        renameAction.setEnabled(editable);
        Action duplicateAction = new TreeDuplicateAction(tree);
        duplicateAction.setEnabled(editable);
        Action[] actions = new Action[]{ copyAction, pasteAction, deleteAction, renameAction, duplicateAction };

        JPopupMenu menu = new JPopupMenu();
        for (Action action : actions) {
            tree.getActionMap().put(action.getValue(Action.NAME), action);
            tree.getInputMap().put(
                    (KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
            menu.add(action);
        }
        return menu;
    }

    public Layer getLayer() {
        return layer;
    }

    public void clearSelection() {
        tree.clearSelection();
    }

    public void addTreeSelectionListener(LayerTreeSelectionListener listener) {
        tree.addTreeSelectionListener(event -> {
            if (eventsEnabled) {
                listener.valueChanged(layer, event);
            }
        });
    }

    public void suppressEvents(Runnable runnable) {
        try {
            this.eventsEnabled = false;
            runnable.run();
        } finally {
            this.eventsEnabled = true;
        }
    }

    public void reload() {
        try {
            StaticTreeNode rootNode = createRootNode(layer.getFile());
            StaticTreeNode currentRoot = (StaticTreeNode) tree.getModel().getRoot();
            rootNode.getChildren().forEach(n -> n.setParent(currentRoot));
            currentRoot.getChildren().clear();
            currentRoot.getChildren().addAll(rootNode.getChildren());
            ((DefaultTreeModel) tree.getModel()).reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
            //TODO error dialog
        }
    }
}
