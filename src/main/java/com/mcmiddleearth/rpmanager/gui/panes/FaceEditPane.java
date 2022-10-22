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
import com.mcmiddleearth.rpmanager.gui.components.NumericStepper;
import com.mcmiddleearth.rpmanager.gui.components.TextInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.Face;
import com.mcmiddleearth.rpmanager.model.FaceDefinition;
import com.mcmiddleearth.rpmanager.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FaceEditPane extends VerticalBox {
    private final Pair<Face, FaceDefinition> entry;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public FaceEditPane(Pair<Face, FaceDefinition> entry) {
        this.entry = entry;

        JPanel facePanel = new JPanel();
        facePanel.setLayout(new BoxLayout(facePanel, BoxLayout.X_AXIS));
        facePanel.add(new JLabel("Face: "));
        JComboBox<Face> faceInput = new JComboBox<>(Face.values());
        faceInput.setRenderer(new FaceRenderer());
        faceInput.setSelectedItem(entry.getLeft());
        faceInput.addItemListener(e -> {
            entry.setLeft((Face) faceInput.getSelectedItem());
            onChange();
        });
        facePanel.add(faceInput);
        this.add(facePanel);
        this.add(new JLabel("UV:"));
        this.add(floatArrayInput(() -> entry.getRight().getUv(), v -> entry.getRight().setUv(v)));

        JPanel texturePanel = new JPanel();
        texturePanel.setLayout(new BoxLayout(texturePanel, BoxLayout.X_AXIS));
        texturePanel.add(new JLabel("Texture: "));
        texturePanel.add(
                new TextInput(entry.getRight().getTexture(), v -> entry.getRight().setTexture(v), e -> onChange()));
        this.add(texturePanel);

        JPanel cullfacePanel = new JPanel();
        cullfacePanel.setLayout(new BoxLayout(cullfacePanel, BoxLayout.X_AXIS));
        cullfacePanel.add(new JLabel("Cullface: "));
        JComboBox<Face> cullfaceInput = new JComboBox<>(Face.values());
        cullfaceInput.setRenderer(new FaceRenderer());
        cullfaceInput.setSelectedItem(entry.getRight().getCullface());
        cullfaceInput.addItemListener(e -> {
            entry.getRight().setCullface((Face) cullfaceInput.getSelectedItem());
            onChange();
        });
        cullfacePanel.add(cullfaceInput);
        this.add(cullfacePanel);

        JPanel rotationPanel = new JPanel();
        rotationPanel.setLayout(new BoxLayout(rotationPanel, BoxLayout.X_AXIS));
        rotationPanel.add(new JLabel("Rotation: "));
        JComboBox<Integer> rotationInput = new JComboBox<>(new Integer[]{0, 90, 180, 270});
        rotationInput.setSelectedItem(entry.getRight().getRotation() == null ? 0 : entry.getRight().getRotation());
        rotationInput.addItemListener(e -> {
            entry.getRight().setRotation((Integer) rotationInput.getSelectedItem());
            if (entry.getRight().getRotation() != null && entry.getRight().getRotation() == 0) {
                entry.getRight().setRotation(null);
            }
            onChange();
        });
        rotationPanel.add(rotationInput);
        this.add(rotationPanel);

        JPanel tintindexPanel = new JPanel();
        tintindexPanel.setLayout(new BoxLayout(tintindexPanel, BoxLayout.X_AXIS));
        tintindexPanel.add(new JLabel("Tint index: "));
        tintindexPanel.add(new NumericStepper(
                entry.getRight().getTintindex() == null ? -1 : entry.getRight().getTintindex(),
                -1, Integer.MAX_VALUE, v -> entry.getRight().setTintindex(v), e -> onChange()));
        this.add(tintindexPanel);
    }

    private JPanel floatArrayInput(Supplier<float[]> getter, Consumer<float[]> setter) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel("x1:"));
        panel.add(new NumberInput(getX1(getter.get()), setX1(getter, setter), e -> onChange()));
        panel.add(new JLabel("y1:"));
        panel.add(new NumberInput(getY1(getter.get()), setY1(getter, setter), e -> onChange()));
        panel.add(new JLabel("x2:"));
        panel.add(new NumberInput(getX2(getter.get()), setX2(getter, setter), e -> onChange()));
        panel.add(new JLabel("y2:"));
        panel.add(new NumberInput(getY2(getter.get()), setY2(getter, setter), e -> onChange()));
        return panel;
    }

    private static class FaceRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ((DefaultListCellRenderer) c).setText(getLabel((Face) value));
            return c;
        }

        private static String getLabel(Face face) {
            if (face == null) {
                return "";
            } else {
                try {
                    return face.getClass().getField(face.name()).getAnnotation(SerializedName.class).value();
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

    private static Float getX1(float[] array) {
        return Optional.ofNullable(array).map(a -> a[0]).orElse(null);
    }

    private static Float getY1(float[] array) {
        return Optional.ofNullable(array).map(a -> a[1]).orElse(null);
    }

    private static Float getX2(float[] array) {
        return Optional.ofNullable(array).map(a -> a[2]).orElse(null);
    }

    private static Float getY2(float[] array) {
        return Optional.ofNullable(array).map(a -> a[3]).orElse(null);
    }

    private static Consumer<Float> setX1(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(0, getter, setter);
    }

    private static Consumer<Float> setY1(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(1, getter, setter);
    }

    private static Consumer<Float> setX2(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(2, getter, setter);
    }

    private static Consumer<Float> setY2(Supplier<float[]> getter, Consumer<float[]> setter) {
        return set(3, getter, setter);
    }

    private static Consumer<Float> set(int index, Supplier<float[]> getter, Consumer<float[]> setter) {
        return v -> {
            float[] value = getter.get();
            if (value == null) {
                value = new float[4];
            }
            value[index] = v;
            if (value[0] == 0.0f && value[1] == 0.0f && value[2] == 0.0f && value[3] == 0.0f) {
                value = null;
            }
            setter.accept(value);
        };
    }
}
