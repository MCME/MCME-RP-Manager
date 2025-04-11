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

package com.mcmiddleearth.rpmanager.gui.modals;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AddBlockstateModal extends JDialog {
    private final Map<String, List<String>> possibleValues;
    private final Action onAccept;

    public AddBlockstateModal(Frame parent, Map<String, List<String>> possibleValues, Action onAccept) {
        super(parent, "Add block state", true);
        this.possibleValues = possibleValues;
        this.onAccept = onAccept;

        setLayout(new BorderLayout());
        AddBlockstateForm addBlockstateForm = new AddBlockstateForm(possibleValues);
        add(addBlockstateForm, BorderLayout.CENTER);
        JButton accept = new JButton(new Action(onAccept.getName(), onAccept.getDescription()) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onAccept.actionPerformed(new ActionEvent(
                        AddBlockstateModal.this, ActionEvent.ACTION_PERFORMED, addBlockstateForm.getSelectedValues()));
                AddBlockstateModal.this.close();
            }
        });
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating variant") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddBlockstateModal.this.close();
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

    private static class AddBlockstateForm extends JPanel {
        private final Map<String, JComboBox<String>> inputs = new LinkedHashMap<>();

        public AddBlockstateForm(Map<String, List<String>> possibleValues) {
            setLayout(new GridBagLayout());
            int y = 0;

            for (Map.Entry<String, List<String>> state : possibleValues.entrySet()) {
                add(new JLabel(state.getKey()), label(y));
                Vector<String> values = new Vector<>(state.getValue());
                values.add(0, null);
                JComboBox<String> comboBox = new JComboBox<>(values);
                inputs.put(state.getKey(), comboBox);
                add(comboBox, input(y));
                y++;
            }
        }

        public String getSelectedValues() {
            return inputs.entrySet().stream()
                    .filter(e -> e.getValue().getSelectedItem() != null)
                    .map(e -> e.getKey() + "=" + e.getValue().getSelectedItem())
                    .collect(Collectors.joining(","));
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
