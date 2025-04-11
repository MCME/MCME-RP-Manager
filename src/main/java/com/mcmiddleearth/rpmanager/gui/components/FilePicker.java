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

package com.mcmiddleearth.rpmanager.gui.components;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import java.awt.*;

public class FilePicker extends JPanel {
    private final JFileChooser fileChooser;
    private final JTextField textField;

    public FilePicker(FileFilter... filters) {
        fileChooser = new JFileChooser();
        for (FileFilter filter : filters) {
            fileChooser.addChoosableFileFilter(filter);
        }
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileHidingEnabled(false);
        configureFileChooser(fileChooser);
        textField = new JTextField(40);
        textField.setEditable(false);
        JButton button = new JButton("Browse...");
        button.addActionListener(actionEvent -> {
            if (fileChooser.showOpenDialog(FilePicker.this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        add(textField);
        add(button);
    }

    public Document getDocument() {
        return textField.getDocument();
    }

    public String getSelectedFilePath() {
        return textField.getText();
    }

    protected void configureFileChooser(JFileChooser fileChooser) {}
}
