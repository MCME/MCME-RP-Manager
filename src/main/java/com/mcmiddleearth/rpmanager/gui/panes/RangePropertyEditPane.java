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
import com.mcmiddleearth.rpmanager.model.internal.RangePropertyType;
import com.mcmiddleearth.rpmanager.model.item.range.CustomModelDataRangeProperty;
import com.mcmiddleearth.rpmanager.model.item.range.RangeProperty;

import javax.swing.*;

public class RangePropertyEditPane extends VerticalBox {
    private final RangeProperty rangeProperty;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public RangePropertyEditPane(RangeProperty rangeProperty) {
        this.rangeProperty = rangeProperty;

        JPanel propertyPanel = new JPanel();
        propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.X_AXIS));
        propertyPanel.add(new JLabel("Property: "));
        propertyPanel.add(Box.createHorizontalGlue());
        propertyPanel.add(new JLabel(RangePropertyType.byRangeProperty(rangeProperty).getId()));
        this.add(propertyPanel);

        if (rangeProperty instanceof CustomModelDataRangeProperty customModelDataRangeProperty) {
            JPanel indexPanel = new JPanel();
            indexPanel.setLayout(new BoxLayout(indexPanel, BoxLayout.X_AXIS));
            indexPanel.add(new JLabel("Index: "));
            NumberInput indexInput = new NumberInput(customModelDataRangeProperty.getIndex(), null,
                    customModelDataRangeProperty::setIndex,
                    input -> onChange(new ChangeEvent(input, input.getText())));
            indexPanel.add(indexInput);
            this.add(indexPanel);
        } else {
            this.add(new JLabel("This range property type is not supported."));
        }
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
