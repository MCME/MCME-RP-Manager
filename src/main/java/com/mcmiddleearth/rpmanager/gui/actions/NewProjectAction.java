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

import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.modals.NewProjectModal;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class NewProjectAction extends Action {
    protected NewProjectAction() {
        super("New project...", Icons.NEW_PROJECT, "Create new project", KeyEvent.VK_N,
                KeyEvent.VK_N);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MainWindow mainWindow = MainWindow.getInstance();
        new NewProjectModal(mainWindow, mainWindow.getSession());
    }
}
