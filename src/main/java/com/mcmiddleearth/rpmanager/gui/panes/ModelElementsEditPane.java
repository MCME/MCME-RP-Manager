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
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.BaseModel;
import com.mcmiddleearth.rpmanager.model.Element;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class ModelElementsEditPane extends VerticalBox {
    private final BaseModel baseModel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ModelElementsEditPane(BaseModel baseModel) {
        this.baseModel = baseModel;

        if (baseModel.getElements() != null && !baseModel.getElements().isEmpty()) {
            boolean collapsed = baseModel.getElements().size() != 1;
            for (Element entry : baseModel.getElements()) {
                this.add(createElementEntry(entry, collapsed));
            }
        }
    }

    public void addElementEntry() {
        Element entry = new Element();
        if (baseModel.getElements() == null) {
            baseModel.setElements(new LinkedList<>());
        }
        baseModel.getElements().add(entry);
        this.add(createElementEntry(entry, false));
        revalidate();
        repaint();
        onChange();
    }

    private CollapsibleSection createElementEntry(Element entry, boolean collapsed) {
        ElementEditPane elementEditPane = new ElementEditPane(entry);
        elementEditPane.addChangeListener(e -> onChange());
        return new CollapsibleSection("Element", elementEditPane, collapsed, removeElementButton(entry));
    }

    private JButton removeElementButton(Element entry) {
        return new JButton(new Action("-", "Remove element") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = baseModel.getElements().indexOf(entry);
                baseModel.getElements().remove(index);
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
