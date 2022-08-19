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

import com.mcmiddleearth.rpmanager.gui.components.tree.JarTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.ResourcePackTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.actions.TreeCopyAction;
import com.mcmiddleearth.rpmanager.gui.components.tree.actions.TreePasteAction;
import com.mcmiddleearth.rpmanager.gui.listeners.LayerTreeSelectionListener;
import com.mcmiddleearth.rpmanager.model.project.Layer;

import javax.swing.*;
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
        if (file.getName().endsWith(".jar")) {
            JTree tree = new JTree(JarTreeFactory.createRootNode(file));
            tree.setComponentPopupMenu(createPopupMenu(tree, false));
            return tree;
        } else {
            JTree tree = new JTree(ResourcePackTreeFactory.createRootNode(file));
            tree.setComponentPopupMenu(createPopupMenu(tree, true));
            return tree;
        }
    }

    private static JPopupMenu createPopupMenu(JTree tree, boolean pasteAvailable) {
        Action copyAction = new TreeCopyAction(tree);
        tree.getActionMap().put(copyAction.getValue(Action.NAME), copyAction);
        tree.getInputMap().put(
                (KeyStroke) copyAction.getValue(Action.ACCELERATOR_KEY), copyAction.getValue(Action.NAME));

        Action pasteAction = new TreePasteAction(tree);
        pasteAction.setEnabled(pasteAvailable);
        tree.getActionMap().put(pasteAction.getValue(Action.NAME), pasteAction);
        tree.getInputMap().put(
                (KeyStroke) pasteAction.getValue(Action.ACCELERATOR_KEY), pasteAction.getValue(Action.NAME));
        JPopupMenu menu = new JPopupMenu();
        menu.add(copyAction);
        menu.add(pasteAction);
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
}
