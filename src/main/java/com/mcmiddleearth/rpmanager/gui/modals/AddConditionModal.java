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

package com.mcmiddleearth.rpmanager.gui.modals;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.MultiValueComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AddConditionModal extends JDialog {
    public AddConditionModal(Frame parent, Map<String, List<String>> possibleValues,
                             Consumer<Map<String, Object>> onAccept) {
        super(parent, "Add condition", true);

        setLayout(new BorderLayout());
        AddConditionForm addConditionForm = new AddConditionForm(possibleValues);
        add(addConditionForm, BorderLayout.CENTER);
        JButton accept = new JButton(new Action("Save", "Save condition") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onAccept.accept(addConditionForm.getSelectedValues());
                AddConditionModal.this.close();
            }
        });
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating condition") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddConditionModal.this.close();
            }
        });
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonsPanel.add(accept);
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        setLocation(MainWindow.getInstance().getMousePosition());
        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class AddConditionForm extends JPanel {
        private final Map<String, MultiValueComboBox> inputs = new LinkedHashMap<>();

        public AddConditionForm(Map<String, List<String>> possibleValues) {
            setLayout(new GridBagLayout());
            int y = 0;
            int maxStringWidth = possibleValues.values().stream()
                    .map(l -> String.join("|", l))
                    .mapToInt(s -> getFontMetrics(getFont()).stringWidth(s))
                    .max().orElse(140);

            for (Map.Entry<String, List<String>> state : possibleValues.entrySet()) {
                add(new JLabel(state.getKey()), label(y));

                MultiValueComboBox comboBox = new MultiValueComboBox(state.getValue(), "-any-");
                comboBox.setPreferredSize(
                        new Dimension(maxStringWidth + 20, (int) comboBox.getPreferredSize().getHeight()));
                inputs.put(state.getKey(), comboBox);
                add(comboBox, input(y));
                y++;
            }
        }

        public Map<String, Object> getSelectedValues() {
            return inputs.entrySet().stream()
                    .map(e -> Map.entry(e.getKey(), e.getValue().getSelectedValues()))
                    .filter(e -> !e.getValue().isEmpty())
                    .map(e -> Map.entry(e.getKey(), String.join("|", e.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
    }
}
