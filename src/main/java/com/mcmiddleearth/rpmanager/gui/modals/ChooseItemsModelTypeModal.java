/*
 * Copyright (C) 2025 MCME
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
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.internal.ItemsModelType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ChooseItemsModelTypeModal extends JDialog {
    public ChooseItemsModelTypeModal(Frame parent, ItemsModelType currentValue, Consumer<ItemsModelType> onAccept,
                                     boolean showWarning) {
        super(parent, "Choose model type", true);

        setLayout(new BorderLayout());

        VerticalBox box = new VerticalBox();
        if (showWarning) {
            box.add(new JLabel("WARNING: Changing model type will discard all data " +
                    "associated with currently selected type!"));
        }
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
        typePanel.add(new JLabel("Model type: "));
        JComboBox<ItemsModelType> typeComboBox = new JComboBox<>(ItemsModelType.values());
        typeComboBox.setRenderer(new ItemsModelListCellRenderer());
        typeComboBox.setSelectedItem(currentValue);
        typePanel.add(typeComboBox);
        box.add(typePanel);
        this.add(box);

        JButton accept = new JButton(new Action("Confirm", "Confirm model type selection") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onAccept.accept((ItemsModelType) typeComboBox.getSelectedItem());
                ChooseItemsModelTypeModal.this.close();
            }
        });
        accept.setEnabled(false);
        JButton cancel = new JButton(new Action("Cancel", "Cancel") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChooseItemsModelTypeModal.this.close();
            }
        });
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonsPanel.add(accept);
        buttonsPanel.add(cancel);
        this.add(buttonsPanel, BorderLayout.PAGE_END);

        typeComboBox.addItemListener(e -> {
            accept.setEnabled(typeComboBox.getSelectedItem() != null && typeComboBox.getSelectedItem() != currentValue);
            revalidate();
            repaint();
        });

        setLocation(MainWindow.getInstance().getMousePosition());
        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class ItemsModelListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ((DefaultListCellRenderer) c).setText(getLabel((ItemsModelType) value));
            return c;
        }

        private String getLabel(ItemsModelType element) {
            return element == null ? "" : element.getId();
        }
    }
}
