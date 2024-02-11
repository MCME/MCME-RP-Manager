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

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.model.BaseModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.internal.RelatedFiles;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.model.project.Project;
import com.mcmiddleearth.rpmanager.utils.ActionManager;
import com.mcmiddleearth.rpmanager.utils.FileLoader;
import com.mcmiddleearth.rpmanager.utils.ResourcePackUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.IOException;

public class ProjectPane extends JPanel {
    private final Project project;
    private final ProjectFilesPane treesPane;
    private final FileEditPane fileEditPane;
    private final RelatedFilesPane relatedFilesPane;
    private final ActionManager actionManager;

    public ProjectPane(Project project) throws IOException, GitAPIException {
        this.project = project;
        this.actionManager = new ActionManager(this::reload);

        setLayout(new BorderLayout());

        this.treesPane = new ProjectFilesPane(project);
        this.fileEditPane = new FileEditPane(actionManager);
        this.relatedFilesPane = new RelatedFilesPane();
        treesPane.addTreeSelectionListener(this::onTreeSelectionChanged);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                new FastScrollPane(treesPane),
                this.fileEditPane);
        innerSplitPane.setDividerSize(1);
        innerSplitPane.setOneTouchExpandable(false);
        innerSplitPane.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                innerSplitPane,
                relatedFilesPane);
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.66);

        add(splitPane, BorderLayout.CENTER);
    }

    public void onTreeSelectionChanged(Layer layer, JTree tree, TreeSelectionEvent event) {
        TreePath newPath = event.getNewLeadSelectionPath();
        try {
            SelectedFileData fileData = newPath == null ? null : FileLoader.load(layer, newPath.getPath());
            fileEditPane.setSelectedFile(fileData,
                    newPath == null ? null : (StaticTreeNode) newPath.getLastPathComponent());
            fileEditPane.setCurrentTree(tree);
            updateRelatedFiles(fileData);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Unknown error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRelatedFiles(SelectedFileData fileData) {
        try {
            if (fileData != null && fileData.getData() != null) {
                if (fileData.getData() instanceof BlockState blockState) {
                    RelatedFiles relatedFiles = ResourcePackUtils.getRelatedFiles(blockState, project);
                    relatedFilesPane.setRelatedFiles(relatedFiles);
                } else if (fileData.getData() instanceof BaseModel model) {
                    RelatedFiles relatedFiles = ResourcePackUtils.getRelatedFiles(model, project);
                    relatedFilesPane.setRelatedFiles(relatedFiles);
                } else {
                    relatedFilesPane.setRelatedFiles(null);
                }
            } else {
                relatedFilesPane.setRelatedFiles(null);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Unknown error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            relatedFilesPane.setRelatedFiles(null);
        }
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public void reload() {
        treesPane.reload();
    }
}
