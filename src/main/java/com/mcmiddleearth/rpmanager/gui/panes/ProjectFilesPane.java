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

import com.mcmiddleearth.rpmanager.events.ListItemAddedEvent;
import com.mcmiddleearth.rpmanager.events.ListItemRemovedEvent;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.listeners.LayerTreeSelectionListener;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.model.project.Project;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProjectFilesPane extends JPanel {
    private final JFileChooser fileChooser = new JFileChooser();
    private final Project project;
    private final List<LayerFilesPane> layerFilesPanes = new LinkedList<>();
    private final List<LayerTreeSelectionListener> treeSelectionListeners = new LinkedList<>();

    public ProjectFilesPane(Project project) throws IOException, GitAPIException {
        this.project = project;

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Resource pack metadata file", "mcmeta"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileHidingEnabled(false);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentY(TOP_ALIGNMENT);

        for (Layer layer : project.getLayers()) {
            LayerFilesPane layerFilesPane = new LayerFilesPane(layer);
            layerFilesPane.setAlignmentY(TOP_ALIGNMENT);
            add(layerFilesPane);
            layerFilesPanes.add(layerFilesPane);
            layerFilesPane.addTreeSelectionListener(this::onFileSelectionChanged);
        }
        JButton addLayerButton = new JButton(new Action("+", "Add resource pack layer") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser.showOpenDialog(ProjectFilesPane.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    project.addLayer(file.getParentFile().getName(), file);
                }
            }
        });
        addLayerButton.setAlignmentY(TOP_ALIGNMENT);
        add(addLayerButton);

        project.addLayerAddedListener(this::onLayerAdded);
        project.addLayerRemovedListener(this::onLayerRemoved);
    }

    @SuppressWarnings("unchecked")
    private void onLayerAdded(ListItemAddedEvent event) {
        try {
            LayerFilesPane layerFilesPane = new LayerFilesPane((Layer) event.getItem());
            layerFilesPane.setAlignmentY(TOP_ALIGNMENT);
            add(layerFilesPane, event.getIndex());
            layerFilesPanes.add(event.getIndex(), layerFilesPane);
            layerFilesPane.addTreeSelectionListener(this::onFileSelectionChanged);
            revalidate();
            repaint();
        } catch (IOException | GitAPIException e) {
            //TODO error dialog
            ((List<Layer>) event.getSource()).remove(event.getIndex());
        }
    }

    private void onLayerRemoved(ListItemRemovedEvent event) {
        remove(event.getIndex());
        layerFilesPanes.remove(event.getIndex());
        revalidate();
        repaint();
    }

    private void onFileSelectionChanged(Layer layer, JTree tree, TreeSelectionEvent event) {
        for (LayerFilesPane pane : layerFilesPanes) {
            if (pane.getLayer() != layer) {
                pane.suppressEvents(pane::clearSelection);
            }
        }
        for (LayerTreeSelectionListener listener : treeSelectionListeners) {
            listener.valueChanged(layer, tree, event);
        }
    }

    public void addTreeSelectionListener(LayerTreeSelectionListener listener) {
        this.treeSelectionListeners.add(listener);
    }

    public void reload() {
        layerFilesPanes.forEach(LayerFilesPane::reload);
    }

    public void setSelectedNode(Object[] path) {
        for (LayerFilesPane layerFilesPane : layerFilesPanes) {
            StaticTreeNode staticTreeNode = layerFilesPane.findNode(path);
            if (staticTreeNode != null) {
                layerFilesPane.setSelectedNode(staticTreeNode);
                break;
            }
        }
    }
}
