/*
 * Copyright (C) 2023 MCME
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

package com.mcmiddleearth.rpmanager.gui.actions;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.utils.ResourcePackUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class CompileAction extends Action {
    protected CompileAction() {
        super("Compile resource pack...", null,
                "Compile project into a single resource pack", KeyEvent.VK_C, KeyEvent.VK_B);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ZIP file", "zip"));
        fileChooser.setDialogTitle("Save resource pack");
        int selection = fileChooser.showSaveDialog(MainWindow.getInstance());
        if (selection == JFileChooser.APPROVE_OPTION) {
            try {
                ResourcePackUtils.compileProject(MainWindow.getInstance().getCurrentProject(),
                        fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Resource pack compiled!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Failed to compile resource pack", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
