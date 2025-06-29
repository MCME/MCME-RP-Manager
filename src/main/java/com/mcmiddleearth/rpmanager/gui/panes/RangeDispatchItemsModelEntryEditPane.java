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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.components.NumberInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.item.EmptyItemsModel;
import com.mcmiddleearth.rpmanager.model.item.RangeDispatchItemsModel;

import javax.swing.*;

public class RangeDispatchItemsModelEntryEditPane extends VerticalBox {
    private final RangeDispatchItemsModel.Entry entry;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public RangeDispatchItemsModelEntryEditPane(RangeDispatchItemsModel.Entry entry) {
        this.entry = entry;

        JPanel thresholdPanel = new JPanel();
        thresholdPanel.setLayout(new BoxLayout(thresholdPanel, BoxLayout.X_AXIS));
        thresholdPanel.add(new JLabel("Threshold: "));
        thresholdPanel.add(new NumberInput(entry.getThreshold(),
                entry::setThreshold,
                input -> onChange(new ChangeEvent(input, input.getText()))));
        this.add(thresholdPanel);

        if (entry.getModel() == null) {
            entry.setModel(new EmptyItemsModel());
        }
        ItemsModelEditPane itemsModelEditPane = new ItemsModelEditPane(entry.getModel());
        itemsModelEditPane.addChangeListener(this::onChange);
        itemsModelEditPane.addModelTypeChangeListener(e -> entry.setModel(e.getNewValue()));
        this.add(itemsModelEditPane);
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    public RangeDispatchItemsModel.Entry getEntry() {
        return entry;
    }
}
