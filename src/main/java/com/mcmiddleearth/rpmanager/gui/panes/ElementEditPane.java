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
import com.mcmiddleearth.rpmanager.gui.components.NumberInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.Element;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ElementEditPane extends VerticalBox {
    private final Element element;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ElementEditPane(Element element) {
        this.element = element;

        this.add(new JLabel("From:"));
        this.add(floatArrayInput(element::getFrom, element::setFrom));
        this.add(new JLabel("To:"));
        this.add(floatArrayInput(element::getTo, element::setTo));
        RotationEditPane rotationEditPane = new RotationEditPane(element);
        rotationEditPane.addChangeListener(e -> onChange());
        this.add(new CollapsibleSection("Rotation", rotationEditPane, element.getRotation() == null));
        JCheckBox shadeInput = new JCheckBox("Shade", Optional.ofNullable(element.getShade()).orElse(true));
        shadeInput.addItemListener(e -> {
            element.setShade(shadeInput.isSelected() ? null : false);
            onChange();
        });
        this.add(shadeInput);

        ElementFacesEditPane facesEditPane = new ElementFacesEditPane(element);
        facesEditPane.addChangeListener(e -> onChange());
        CollapsibleSection facesEditSection =
                new CollapsibleSection("faces: {", facesEditPane, false,
                        new JButton(new Action("+", "Add face") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                facesEditPane.addFaceEntry();
                            }
                        }));
        this.add(facesEditSection);
        this.add(new JLabel("}"));

        this.add(new JSeparator());
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
