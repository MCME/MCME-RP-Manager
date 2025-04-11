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
import com.mcmiddleearth.rpmanager.gui.components.NumberInput;
import com.mcmiddleearth.rpmanager.gui.components.NumericStepper;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.Predicate;

import javax.swing.*;
import java.util.function.Consumer;

public class PredicateEditPane extends VerticalBox {
    private final Predicate predicate;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public PredicateEditPane(Predicate predicate) {
        this.predicate = predicate;

        this.add(floatInput("Angle: ", predicate.getAngle(), predicate::setAngle));
        this.add(zeroOrOneIntegerInput("Blocking: ", predicate.getBlocking(), predicate::setBlocking));
        this.add(zeroOrOneIntegerInput("Broken: ", predicate.getBroken(), predicate::setBroken));
        this.add(zeroOrOneIntegerInput("Cast: ", predicate.getCast(), predicate::setCast));
        this.add(floatInput("Cooldown: ", predicate.getCooldown(), predicate::setCooldown));
        this.add(floatInput("Damage: ", predicate.getDamage(), predicate::setDamage));
        this.add(zeroOrOneIntegerInput("Damaged: ", predicate.getDamaged(), predicate::setDamaged));
        this.add(zeroOrOneIntegerInput("Left handed: ", predicate.getLefthanded(), predicate::setLefthanded));
        this.add(floatInput("Pull: ", predicate.getPull(), predicate::setPull));
        this.add(zeroOrOneIntegerInput("Pulling: ", predicate.getPulling(), predicate::setPulling));
        this.add(zeroOrOneIntegerInput("Charged: ", predicate.getCharged(), predicate::setCharged));
        this.add(zeroOrOneIntegerInput("Firework: ", predicate.getFirework(), predicate::setFirework));
        this.add(zeroOrOneIntegerInput("Throwing: ", predicate.getThrowing(), predicate::setThrowing));
        this.add(floatInput("Time: ", predicate.getTime(), predicate::setTime));

        JPanel customModelDataPanel = new JPanel();
        customModelDataPanel.setLayout(new BoxLayout(customModelDataPanel, BoxLayout.X_AXIS));
        customModelDataPanel.add(new JLabel("Custom model data: "));
        customModelDataPanel.add(new NumericStepper(
                predicate.getCustomModelData() == null ? -1 : predicate.getCustomModelData(), -1, Integer.MAX_VALUE,
                v -> {
                    if (v != null && v < 0) {
                        predicate.setCustomModelData(null);
                    } else {
                        predicate.setCustomModelData(v);
                    }
                }, e -> onChange()));
        this.add(customModelDataPanel);

        this.add(floatInput("Level: ", predicate.getLevel(), predicate::setLevel));
        this.add(floatInput("Filled: ", predicate.getFilled(), predicate::setFilled));
        this.add(zeroOrOneIntegerInput("Tooting: ", predicate.getTooting(), predicate::setTooting));
    }

    private JPanel floatInput(String label, Float value, Consumer<Float> setter) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(label));
        panel.add(new NumberInput(value, setter, e -> onChange()));
        return panel;
    }

    private JPanel zeroOrOneIntegerInput(String label, Integer value, Consumer<Integer> setter) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(label));
        panel.add(new NumericStepper(value == null ? -1 : value, -1, 1,
                v -> {
                    if (v != null && v < 0) {
                        setter.accept(null);
                    } else {
                        setter.accept(v);
                    }
                }, e -> onChange()));
        return panel;
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, predicate));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
