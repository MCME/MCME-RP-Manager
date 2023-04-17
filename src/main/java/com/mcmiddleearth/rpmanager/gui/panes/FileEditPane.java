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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.ItemModel;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.Action;
import com.mcmiddleearth.rpmanager.utils.ActionManager;
import com.mcmiddleearth.rpmanager.utils.JsonFileLoader;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileEditPane extends JPanel {
    private static final Gson GSON = new GsonBuilder()
            .setLenient().setPrettyPrinting().enableComplexMapKeySerialization().create();
    private final JPanel editPane;
    private final JTextArea previewArea;
    private StaticTreeNode currentNode = null;
    private final ActionManager actionManager;

    public FileEditPane(ActionManager actionManager) {
        this.actionManager = actionManager;
        setLayout(new BorderLayout());

        this.editPane = new JPanel();
        this.editPane.setLayout(new BorderLayout());

        this.previewArea = new JTextArea(0, 0);
        this.previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.previewArea.setEditable(false);
        ((DefaultCaret) this.previewArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JPanel previewPane = new JPanel();
        previewPane.setLayout(new BorderLayout());
        previewPane.add(previewArea, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                editPane,
                new FastScrollPane(previewPane));
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    public void setSelectedFile(Layer layer, Object[] path, StaticTreeNode node) {
        try {
            SelectedFileData fileData = JsonFileLoader.load(layer, path);
            this.currentNode = null;
            setData(fileData);
            this.currentNode = node;
        } catch (IOException e) {
            //TODO show error dialog
        }
    }

    private void setData(SelectedFileData data) {
        editPane.removeAll();
        if (data == null) {
            JLabel label = new JLabel("No file selected, or no editor available for selected file.");
            editPane.add(label, BorderLayout.CENTER);
        } else if (data.getData() instanceof BlockState blockState) {
            updatePreview(GSON.toJson(data.getData()));
            BlockstateFileEditPane blockstateFileEditPane = new BlockstateFileEditPane(data.getName(), blockState);
            blockstateFileEditPane.addChangeListener(this::onChange);
            JScrollPane scrollPane = new FastScrollPane(blockstateFileEditPane);
            editPane.add(scrollPane, BorderLayout.CENTER);
        } else if (data.getData() instanceof BlockModel blockModel) {
            updatePreview(GSON.toJson(data.getData()));
            BlockModelFileEditPane blockModelFileEditPane = new BlockModelFileEditPane(data.getName(), blockModel);
            blockModelFileEditPane.addChangeListener(this::onChange);
            JScrollPane scrollPane = new FastScrollPane(blockModelFileEditPane);
            editPane.add(scrollPane, BorderLayout.CENTER);
        } else if (data.getData() instanceof ItemModel itemModel) {
            updatePreview(GSON.toJson(data.getData()));
            ItemModelFileEditPane itemModelFileEditPane = new ItemModelFileEditPane(data.getName(), itemModel);
            itemModelFileEditPane.addChangeListener(this::onChange);
            JScrollPane scrollPane = new FastScrollPane(itemModelFileEditPane);
            editPane.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel label = new JLabel("No file selected, or no editor available for selected file.");
            editPane.add(label, BorderLayout.CENTER);
        }
        editPane.revalidate();
        editPane.repaint();
    }

    private void onChange(ChangeEvent changeEvent) {
        String newContent = GSON.toJson(changeEvent.getObject());
        updatePreview(newContent);
        updateFile(newContent);
    }

    private void updatePreview(String text) {
        previewArea.setText(text);
    }

    private void updateFile(String text) {
        if (currentNode != null) {
            File file = this.currentNode.getFile();
            Action undoAction = null;
            Action redoAction = null;
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] content = inputStream.readAllBytes();
                undoAction = () -> {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        outputStream.write(content);
                    }
                };
                redoAction = () -> {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        outputStream.write(text.getBytes(StandardCharsets.UTF_8));
                    }
                };
            } catch (IOException e) {
                //TODO display error dialog
            }
            if (undoAction != null && redoAction != null) {
                actionManager.submit(undoAction, redoAction);
            }
        }
    }
}
