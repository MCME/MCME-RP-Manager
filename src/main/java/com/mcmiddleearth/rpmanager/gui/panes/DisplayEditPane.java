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

import com.google.gson.annotations.SerializedName;
import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.components.NumberInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.Display;
import com.mcmiddleearth.rpmanager.model.Position;
import com.mcmiddleearth.rpmanager.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DisplayEditPane extends VerticalBox {
    private final Pair<Position, Display> entry;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public DisplayEditPane(Pair<Position, Display> entry) {
        this.entry = entry;

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.X_AXIS));
        positionPanel.add(new JLabel("Position: "));
        JComboBox<Position> positionInput = new JComboBox<>(Position.values());
        positionInput.setRenderer(new PositionRenderer());
        positionInput.setSelectedItem(entry.getLeft());
        positionInput.addItemListener(e -> {
            entry.setLeft((Position) positionInput.getSelectedItem());
            onChange();
        });
        positionPanel.add(positionInput);
        this.add(positionPanel);
        this.add(new JLabel("Rotation:"));
        this.add(floatArrayInput(() -> entry.getRight().getRotation(), v -> entry.getRight().setRotation(v)));
        this.add(new JLabel("Translation:"));
        this.add(floatArrayInput(() -> entry.getRight().getTranslation(), v -> entry.getRight().setTranslation(v)));
        this.add(new JLabel("Scale:"));
        this.add(floatArrayInput(() -> entry.getRight().getScale(), v -> entry.getRight().setScale(v)));
    }

    private JPanel floatArrayInput(Supplier<float[]> getter, Consumer<float[]> setter) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel("x:"));
        panel.add(new NumberInput(getX(getter.get()), setX(getter, setter), e -> onChange()));
        panel.add(new JLabel("y:"));
        panel.add(new NumberInput(getY(getter.get()), setY(getter, setter), e -> onChange()));
        panel.add(new JLabel("z:"));
        panel.add(new NumberInput(getZ(getter.get()), setZ(getter, setter), e -> onChange()));
        return panel;
    }

    private static class PositionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ((DefaultListCellRenderer) c).setText(getLabel((Position) value));
            return c;
        }

        private static String getLabel(Position position) {
            if (position == null) {
                return "";
            } else {
                try {
                    return position.getClass().getField(position.name()).getAnnotation(SerializedName.class).value();
                } catch (NoSuchFieldException e) {
                    // should never happen
                    return "";
                }
            }
        }
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, null));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    private static Float getX(float[] array) {
        return Optional.ofNullable(array).map(a -> a[0]).orElse(null);
    }

    private static Float getY(float[] array) {
        return Optional.ofNullable(array).map(a -> a[1]).orElse(null);
    }

    private static Float getZ(float[] array) {
        return Optional.ofNullable(array).map(a -> a[2]).orElse(null);
    }

    private static Consumer<Float> setX(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(0, getter, setter);
    }

    private static Consumer<Float> setY(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(1, getter, setter);
    }

    private static Consumer<Float> setZ(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(2, getter, setter);
    }

    private static Consumer<Float> set(int index, Supplier<float[]> getter, Consumer<float[]> setter) {
        return v -> {
            float[] value = getter.get();
            if (value == null) {
                value = new float[3];
            }
            value[index] = v;
            if (value[0] == 0.0f && value[1] == 0.0f && value[2] == 0.0f) {
                value = null;
            }
            setter.accept(value);
        };
    }
}
