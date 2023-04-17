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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RedoAction extends Action {
    protected RedoAction() {
        super("Redo", null, "Redo last action", KeyEvent.VK_R,
                KeyEvent.VK_Y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MainWindow.getInstance().getActionManager().redo();
    }
}
