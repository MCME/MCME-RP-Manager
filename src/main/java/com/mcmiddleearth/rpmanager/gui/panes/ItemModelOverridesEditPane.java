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
import com.mcmiddleearth.rpmanager.model.ItemModel;
import com.mcmiddleearth.rpmanager.model.Override;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class ItemModelOverridesEditPane extends VerticalBox {
    private final ItemModel itemModel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ItemModelOverridesEditPane(ItemModel itemModel) {
        this.itemModel = itemModel;

        if (itemModel.getOverrides() != null && !itemModel.getOverrides().isEmpty()) {
            boolean collapsed = itemModel.getOverrides().size() != 1;
            for (Override entry : itemModel.getOverrides()) {
                this.add(createOverrideEntry(entry, collapsed));
            }
        }
    }

    public void addOverrideEntry() {
        Override entry = new Override();
        if (itemModel.getOverrides() == null) {
            itemModel.setOverrides(new LinkedList<>());
        }
        itemModel.getOverrides().add(entry);
        this.add(createOverrideEntry(entry, false));
        revalidate();
        repaint();
        onChange();
    }

    private CollapsibleSection createOverrideEntry(Override entry, boolean collapsed) {
        OverrideEditPane overrideEditPane = new OverrideEditPane(entry);
        overrideEditPane.addChangeListener(e -> onChange());
        return new CollapsibleSection("Override", overrideEditPane, collapsed, removeOverrideButton(entry));
    }

    private JButton removeOverrideButton(Override entry) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove override") {
            @java.lang.Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = itemModel.getOverrides().indexOf(entry);
                itemModel.getOverrides().remove(index);
                remove(index);
                revalidate();
                repaint();
                onChange();
            }
        });
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, null));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
