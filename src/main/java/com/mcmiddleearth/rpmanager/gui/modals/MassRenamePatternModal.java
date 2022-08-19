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

public class MassRenamePatternModal extends BaseRenameModal {
    public MassRenamePatternModal(Frame parent, JTree tree, List<StaticTreeNode> nodes) {
        super(parent, "Rename files", tree);

        setLayout(new BorderLayout());
        JLabel label = new JLabel("Pattern (use %i for file number, e.g. file_name_%i.ext)");
        add(label, BorderLayout.NORTH);
        MassRenamePatternForm form = new MassRenamePatternForm();
        add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton rename = new JButton(new Action("Rename", "Rename files") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int i = 1;
                for (StaticTreeNode node : nodes) {
                    renameNode(node, form.getPattern(), i++);
                }
                MassRenamePatternModal.this.close();
            }
        });
        new FormButtonEnabledListener(rename.getModel(), form.getDocuments());
        buttonsPanel.add(rename);
        JButton cancel = new JButton(new Action("Cancel", "Cancel renaming files") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MassRenamePatternModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    private void renameNode(StaticTreeNode node, String pattern, int i) {
        renameNode(node, pattern.replaceAll("%i", Integer.toString(i)));
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class MassRenamePatternForm extends Form {
        private final JTextField patternInput;

        private MassRenamePatternForm() {
            patternInput = new JTextField(50);

            addInput(0, patternInput);
        }

        public List<Document> getDocuments() {
            return List.of(patternInput.getDocument());
        }

        public String getPattern() {
            return patternInput.getText();
        }
    }
}
