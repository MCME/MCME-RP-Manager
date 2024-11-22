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

package com.mcmiddleearth.rpmanager.gui.actions;

import com.mcmiddleearth.rpmanager.gui.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ChangeMinecraftVersionAction extends Action {
    protected ChangeMinecraftVersionAction() {
        super("Change minecraft location", null, "Choose new minecraft JAR file location", KeyEvent.VK_C, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(
                MainWindow.getInstance().getCurrentProject().getLayers().get(0).getFile());
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JAR file", "jar"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileHidingEnabled(false);
        fileChooser.setDialogTitle("Choose Minecraft JAR location");
        if (fileChooser.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            MainWindow.getInstance().getCurrentProject().getLayers().get(0).setFile(fileChooser.getSelectedFile());
            MainWindow.getInstance().reload();
        }
    }
}
