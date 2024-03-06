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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//TODO disable action when no project selected
public class SaveProjectAction extends Action {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    protected SaveProjectAction() {
        super("Save project", Icons.SAVE_PROJECT, "Save project", KeyEvent.VK_S,
                KeyEvent.VK_S);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Project project = MainWindow.getInstance().getCurrentProject();
        if (project != null) {
            File f = new File(project.getLocation(), project.getName() + ".rpproject");
            try (FileWriter fileWriter = new FileWriter(f)) {
                gson.toJson(project, fileWriter);
                MainWindow.getInstance().updateRecentProjects(project);
            } catch (IOException e) {
                //TODO error dialog
            }
        }
    }
}
