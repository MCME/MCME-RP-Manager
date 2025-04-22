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
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.*;
import com.mcmiddleearth.rpmanager.model.item.RangeDispatchItemsModel;

import javax.swing.*;

public class ItemFileEditPane extends VerticalBox {
    private final Item item;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ItemFileEditPane(Item item) {
        this.item = item;

        JPanel handAnimationOnSwapPanel = new JPanel();
        handAnimationOnSwapPanel.setLayout(new BoxLayout(handAnimationOnSwapPanel, BoxLayout.X_AXIS));
        handAnimationOnSwapPanel.add(new JLabel("Hand animation on swap: "));
        JComboBox<Boolean> handAnimationOnSwapInput = new JComboBox<>(new Boolean[] { null, false, true });
        handAnimationOnSwapInput.setSelectedItem(item.getHandAnimationOnSwap());
        handAnimationOnSwapInput.addItemListener(event -> {
            item.setHandAnimationOnSwap((Boolean) handAnimationOnSwapInput.getSelectedItem());
            onChange();
        });
        handAnimationOnSwapPanel.add(handAnimationOnSwapInput);
        this.add(handAnimationOnSwapPanel);

        if (item.getModel() == null) {
            item.setModel(new RangeDispatchItemsModel());
        }

        ItemsModelEditPane itemsModelEditPane = new ItemsModelEditPane(item.getModel());
        itemsModelEditPane.addChangeListener(event -> onChange());
        itemsModelEditPane.addModelTypeChangeListener(e -> item.setModel(e.getNewValue()));
        this.add(itemsModelEditPane);
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, item));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
