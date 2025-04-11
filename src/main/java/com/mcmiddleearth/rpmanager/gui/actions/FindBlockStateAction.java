/*
 * Copyright (C) 2025 MCME
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
import com.mcmiddleearth.rpmanager.gui.modals.FindBlockStateModal;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FindBlockStateAction extends Action {
    protected FindBlockStateAction() {
        super("Find block state...", null, "Find block state by its text representation",
                KeyEvent.VK_F, KeyEvent.VK_F);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MainWindow mainWindow = MainWindow.getInstance();
        if (mainWindow.getCurrentProject() != null) {
            new FindBlockStateModal(mainWindow);
        }
    }
}
