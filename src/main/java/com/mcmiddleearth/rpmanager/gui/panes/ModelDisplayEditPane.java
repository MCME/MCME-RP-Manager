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
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.IconButton;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.model.BaseModel;
import com.mcmiddleearth.rpmanager.model.Display;
import com.mcmiddleearth.rpmanager.model.Position;
import com.mcmiddleearth.rpmanager.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelDisplayEditPane extends VerticalBox {
    private final BaseModel baseModel;
    private final List<Pair<Position, Display>> display;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ModelDisplayEditPane(BaseModel baseModel) {
        this.baseModel = baseModel;
        this.display = baseModel.getDisplay() == null ?
                new LinkedList<>() :
                new LinkedList<>(baseModel.getDisplay().entrySet().stream().map(Pair::new).toList());

        if (!display.isEmpty()) {
            boolean collapsed = display.size() != 1;
            for (Pair<Position, Display> entry : display) {
                this.add(createDisplayEntry(entry, collapsed));
            }
        }
    }

    public void addDisplayEntry() {
        Pair<Position, Display> entry = new Pair<>(null, new Display());
        display.add(entry);
        this.add(createDisplayEntry(entry, false));
        revalidate();
        repaint();
        onDisplayChange();
    }

    private CollapsibleSection createDisplayEntry(Pair<Position, Display> entry, boolean collapsed) {
        DisplayEditPane displayEditPane = new DisplayEditPane(entry);
        displayEditPane.addChangeListener(e -> onDisplayChange());
        return new CollapsibleSection("Display", displayEditPane, collapsed, removeDisplayButton(entry));
    }

    private JButton removeDisplayButton(Pair<Position, Display> entry) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove display") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = display.indexOf(entry);
                display.remove(index);
                remove(index);
                revalidate();
                repaint();
                onDisplayChange();
            }
        });
    }

    private void onDisplayChange() {
        Map<Position, Display> map = new LinkedHashMap<>();
        for (Pair<Position, Display> entry : display) {
            if (entry.getLeft() != null) {
                map.put(entry.getLeft(), entry.getRight());
            }
        }
        if (map.isEmpty()) {
            map = null;
        }
        baseModel.setDisplay(map);
        onChange();
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, null));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
