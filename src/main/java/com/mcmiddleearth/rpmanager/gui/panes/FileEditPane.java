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

import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.JsonFileLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FileEditPane extends JPanel {
    private final JLabel tmpLabel;

    public FileEditPane() {
        setLayout(new BorderLayout());

        JPanel editPane = new JPanel();
        JPanel previewPane = new JPanel();

        editPane.add(this.tmpLabel = new JLabel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                editPane,
                previewPane);
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    public void setSelectedFile(Layer layer, Object[] path) {
        String pathStr = "";
        try {
            Object fileData = JsonFileLoader.load(layer, path);
            if (fileData != null) {
                pathStr = fileData.getClass().getCanonicalName();
            }
        } catch (IOException e) {
            //TODO show error dialog
        }
        tmpLabel.setText(pathStr);
        tmpLabel.revalidate();
        tmpLabel.repaint();
    }
}
