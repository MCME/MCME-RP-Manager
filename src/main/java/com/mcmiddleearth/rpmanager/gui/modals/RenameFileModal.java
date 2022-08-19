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

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.Form;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.FormButtonEnabledListener;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RenameFileModal extends BaseRenameModal {
    public RenameFileModal(Frame parent, JTree tree, StaticTreeNode node) {
        super(parent, "Rename file", tree);

        setLayout(new BorderLayout());
        RenameFileForm form = new RenameFileForm(node.getName());
        add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton rename = new JButton(new Action("Rename", "Rename file") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                renameNode(node, form.getNewName());
                RenameFileModal.this.close();
            }
        });
        new FormButtonEnabledListener(rename.getModel(), form.getDocuments());
        buttonsPanel.add(rename);
        JButton cancel = new JButton(new Action("Cancel", "Cancel renaming file") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RenameFileModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class RenameFileForm extends Form {
        private final JTextField nameInput;

        private RenameFileForm(String currentName) {
            nameInput = new JTextField(currentName, 50);

            addLabel(0, "New name");
            addInput(0, nameInput);
        }

        public List<Document> getDocuments() {
            return List.of(nameInput.getDocument());
        }

        public String getNewName() {
            return nameInput.getText();
        }
    }
}
