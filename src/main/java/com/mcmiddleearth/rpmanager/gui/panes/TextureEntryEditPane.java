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

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.components.TextInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.utils.Pair;

import javax.swing.*;

public class TextureEntryEditPane extends VerticalBox {
    private final Pair<String, String> entry;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public TextureEntryEditPane(Pair<String, String> entry) {
        this.entry = entry;

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(new JLabel("Name: "));
        namePanel.add(new TextInput(entry.getLeft(), entry::setLeft, e -> onChange()));
        this.add(namePanel);
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
        pathPanel.add(new JLabel("Path: "));
        pathPanel.add(new TextInput(entry.getRight(), entry::setRight, e -> onChange()));
        this.add(pathPanel);
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, null));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
