/*
 * Copyright (C) 2024 MCME
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
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureFileEditPane extends VerticalBox {
    public TextureFileEditPane(File file, BufferedImage texture) {
        JButton openButton = new JButton(new Action("Open in external editor", "Open image in external editor") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String editor = MainWindow.getInstance().getSettings().getImageEditor();
                if (file != null && editor != null && !editor.isEmpty()) {
                    try {
                        new ProcessBuilder(editor, file.getAbsolutePath()).start();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MainWindow.getInstance(), "Unknown error: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        add(openButton);

        int scale = Math.min(256 / texture.getWidth(), 256 / texture.getHeight());
        Image scaled = scale > 0 ? texture.getScaledInstance(
                scale * texture.getWidth(), scale * texture.getHeight(), Image.SCALE_FAST) : texture;
        JLabel imageLabel = new JLabel();
        add(imageLabel);
        imageLabel.setIcon(new ImageIcon(scaled));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
    }
}
