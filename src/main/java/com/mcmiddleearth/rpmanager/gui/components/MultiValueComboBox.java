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

package com.mcmiddleearth.rpmanager.gui.components;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiValueComboBox extends JButton {
    private final Map<String, Boolean> values = new LinkedHashMap<>();
    private final String noSelectionLabel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public MultiValueComboBox(List<String> values) {
        this(values, Collections.emptyList());
    }

    public MultiValueComboBox(List<String> values, String noSelectionLabel) {
        this(values, Collections.emptyList(), noSelectionLabel);
    }

    public MultiValueComboBox(List<String> values, List<String> selectedValues) {
        this(values, selectedValues, "-none-");
    }

    public MultiValueComboBox(List<String> values, List<String> selectedValues, String noSelectionLabel) {
        this.noSelectionLabel = noSelectionLabel;

        for (String value : values) {
            this.values.put(value, selectedValues.contains(value));
        }

        setText(computeName());

        JPopupMenu menu = new JPopupMenu();
        for (Map.Entry<String, Boolean> entry : this.values.entrySet()) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(entry.getKey(), entry.getValue());
            menuItem.addItemListener(e -> {
                this.values.put(entry.getKey(), menuItem.isSelected());
                updateName();
                eventDispatcher.dispatchEvent(new ChangeEvent(this, getText()));
            });
            menu.add(menuItem);
        }

        addActionListener(e -> {
            if (!menu.isVisible()) {
                Point p = getLocationOnScreen();
                menu.setInvoker(this);
                menu.setLocation((int) p.getX(), (int) p.getY() + getHeight());
                menu.setVisible(true);
            } else {
                menu.setVisible(false);
            }
        });
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    public List<String> getSelectedValues() {
        return values.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private String computeName() {
        String name = values.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(Collectors.joining("|"));
        return name.isEmpty() ? noSelectionLabel : name;
    }

    private void updateName() {
        setText(computeName());
        revalidate();
        repaint();
    }
}
