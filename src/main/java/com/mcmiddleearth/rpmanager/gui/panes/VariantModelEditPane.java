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
import com.mcmiddleearth.rpmanager.model.Model;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class VariantModelEditPane extends JPanel {
    private final Model model;
    private final JTextField modelNameInput;
    private final JComboBox<Integer> xInput;
    private final JComboBox<Integer> yInput;
    private final JCheckBox uvLockInput;
    private final JSpinner weightInput;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public VariantModelEditPane(Model model) {
        this.model = model;

        setLayout(new GridBagLayout());

        modelNameInput = new JTextField(model.getModel());
        modelNameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                model.setModel(modelNameInput.getText());
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                model.setModel(modelNameInput.getText());
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                model.setModel(modelNameInput.getText());
                onChange();
            }
        });
        xInput = new JComboBox<>(new Integer[] { null, 90, 180, 270 });
        xInput.setSelectedItem(model.getX());
        xInput.addItemListener(itemEvent -> {
            model.setX((Integer) xInput.getSelectedItem());
            onChange();
        });
        yInput = new JComboBox<>(new Integer[] { null, 90, 180, 270 });
        yInput.setSelectedItem(model.getY());
        yInput.addItemListener(itemEvent -> {
            model.setY((Integer) yInput.getSelectedItem());
            onChange();
        });
        uvLockInput = new JCheckBox();
        uvLockInput.setSelected(Boolean.TRUE.equals(model.getUvlock()));
        uvLockInput.addItemListener(itemEvent -> {
            model.setUvlock(uvLockInput.isSelected());
            onChange();
        });
        weightInput = new JSpinner(
                new SpinnerNumberModel(model.getWeight() == null ? 1 : model.getWeight(), 1, Integer.MAX_VALUE, 1));
        ((JSpinner.DefaultEditor) weightInput.getEditor()).getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                model.setWeight(weightInput.getValue() == null ? null : ((Number) weightInput.getValue()).intValue());
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                model.setWeight(weightInput.getValue() == null ? null : ((Number) weightInput.getValue()).intValue());
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                model.setWeight(weightInput.getValue() == null ? null : ((Number) weightInput.getValue()).intValue());
                onChange();
            }
        });

        add(new JLabel("Model"), label(0));
        add(modelNameInput, input(0));
        add(new JLabel("X-axis rotation"), label(1));
        add(xInput, input(1));
        add(new JLabel("Y-axis rotation"), label(2));
        add(yInput, input(2));
        add(new JLabel("UV lock"), label(3));
        add(uvLockInput, input(3));
        add(new JLabel("Weight"), label(4));
        add(weightInput, input(4));
    }

    private static GridBagConstraints label(int y) {
        return new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0);
    }

    private static GridBagConstraints input(int y) {
        return new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, model));
    }

    public Model getModel() {
        return model;
    }
}
