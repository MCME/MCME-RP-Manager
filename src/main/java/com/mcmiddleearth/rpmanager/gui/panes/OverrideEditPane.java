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
import com.mcmiddleearth.rpmanager.gui.components.*;
import com.mcmiddleearth.rpmanager.model.Override;
import com.mcmiddleearth.rpmanager.model.Predicate;

import javax.swing.*;

public class OverrideEditPane extends VerticalBox {
    private final Override override;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public OverrideEditPane(Override override) {
        this.override = override;
        if (override.getPredicate() == null) {
            override.setPredicate(new Predicate());
        }

        PredicateEditPane predicateEditPane = new PredicateEditPane(override.getPredicate());
        predicateEditPane.addChangeListener(e -> onChange());
        CollapsibleSection collapsibleSection = new CollapsibleSection("Predicate", predicateEditPane, true);
        this.add(collapsibleSection);

        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.X_AXIS));
        modelPanel.add(new JLabel("Model: "));
        modelPanel.add(new TextInput(override.getModel(), override::setModel, e -> onChange()));
        this.add(modelPanel);
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, override));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
