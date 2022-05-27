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
import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.ItemModel;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.JsonFileLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FileEditPane extends JPanel {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private final JPanel editPane;
    private final JTextArea previewArea;

    public FileEditPane() {
        setLayout(new BorderLayout());

        this.editPane = new JPanel();
        this.editPane.setLayout(new BorderLayout());

        this.previewArea = new JTextArea(0, 0);
        this.previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.previewArea.setEditable(false);

        JPanel previewPane = new JPanel();
        previewPane.setLayout(new BorderLayout());
        previewPane.add(previewArea, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                editPane,
                new JScrollPane(previewPane));
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    public void setSelectedFile(Layer layer, Object[] path) {
        try {
            SelectedFileData fileData = JsonFileLoader.load(layer, path);
            setData(fileData);
        } catch (IOException e) {
            //TODO show error dialog
        }
    }

    private void setData(SelectedFileData data) {
        editPane.removeAll();
        if (data.getData() instanceof BlockState) {
            updatePreview(data.getData());
            BlockstateFileEditPane blockstateFileEditPane =
                    new BlockstateFileEditPane(data.getName(), (BlockState) data.getData());
            blockstateFileEditPane.addChangeListener(this::onChange);
            JScrollPane scrollPane = new JScrollPane(blockstateFileEditPane);
            editPane.add(scrollPane, BorderLayout.CENTER);
        } else if (data.getData() instanceof BlockModel) {
            //TODO
        } else if (data.getData() instanceof ItemModel) {
            //TODO
        } else {
            JLabel label = new JLabel("No file selected, or no editor available for selected file.");
            editPane.add(label, BorderLayout.CENTER);
        }
        editPane.revalidate();
        editPane.repaint();
    }

    private void onChange(ChangeEvent changeEvent) {
        updatePreview(changeEvent.getObject());
    }

    private void updatePreview(Object o) {
        previewArea.setText(GSON.toJson(o));
    }
}
