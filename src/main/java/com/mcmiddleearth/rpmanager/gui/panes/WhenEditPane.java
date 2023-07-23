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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.components.Form;
import com.mcmiddleearth.rpmanager.gui.components.MultiValueComboBox;
import com.mcmiddleearth.rpmanager.utils.BlockStateUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WhenEditPane extends Form {
    private final Map<String, Object> condition;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public WhenEditPane(String fileName, Map<String, Object> condition) {
        this.condition = condition;
        int y = 0;

        Map<String, List<String>> possibleStates = BlockStateUtils.getPossibleStates(fileName);

        int maxStringWidth = possibleStates.values().stream()
                .map(l -> String.join("|", l))
                .mapToInt(s -> getFontMetrics(getFont()).stringWidth(s))
                .max().orElse(140);

        for (Map.Entry<String, List<String>> entry : possibleStates.entrySet()) {
            addLabel(y, entry.getKey());
            MultiValueComboBox multiValueComboBox = new MultiValueComboBox(
                    entry.getValue(),
                    Optional.ofNullable(condition.get(entry.getKey()))
                            .map(o -> List.of(o.toString().split("\\|"))).orElse(Collections.emptyList()),
                    "-any-");
            multiValueComboBox.setPreferredSize(
                    new Dimension(maxStringWidth + 20, (int) multiValueComboBox.getPreferredSize().getHeight()));
            multiValueComboBox.addChangeListener((EventListener<ChangeEvent>) e -> {
                if (multiValueComboBox.getSelectedValues().isEmpty()) {
                    condition.remove(entry.getKey());
                } else {
                    condition.put(entry.getKey(), String.join("|", multiValueComboBox.getSelectedValues()));
                }
                eventDispatcher.dispatchEvent(new ChangeEvent(this, condition));
            });
            addInput(y, multiValueComboBox);
            y++;
        }
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
