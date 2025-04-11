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

package com.mcmiddleearth.rpmanager.gui.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.model.project.Project;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class OpenProjectAction extends Action {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private final JFileChooser fileChooser;

    protected OpenProjectAction() {
        super("Open project...", Icons.OPEN_PROJECT, "Open project", KeyEvent.VK_O,
                KeyEvent.VK_O);

        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Resource pack manager project", "rpproject"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileHidingEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (fileChooser.showOpenDialog(MainWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
            doOpenProject(fileChooser.getSelectedFile());
        }
    }

    public static void doOpenProject(File projectFile) {
        try (FileReader fileReader = new FileReader(projectFile)) {
            Project project = GSON.fromJson(fileReader, Project.class);
            project.setProjectFile(projectFile);
            project.setLocation(projectFile.getParentFile());
            project.setName(projectFile.getName().substring(0, projectFile.getName().length() - 10));
            MainWindow.getInstance().getSession().addProject(project);
            MainWindow.getInstance().updateRecentProjects(project);
        } catch (IOException e) {
            //TODO error dialog
        }
    }
}
