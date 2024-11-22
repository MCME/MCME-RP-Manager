/*
 * Copyright (C) 2024 MCME
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
import com.mcmiddleearth.rpmanager.gui.components.Form;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.FormButtonEnabledListener;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

public class ReplaceInFilesModal extends JDialog {
    public ReplaceInFilesModal(Frame parent, List<StaticTreeNode> nodes) {
        super(parent, "Replace in files", true);

        setLayout(new BorderLayout());

        ReplaceInFilesForm form = new ReplaceInFilesForm();
        add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton replace = new JButton(new Action("Replace", "Replace specified text in all selected files") {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
                com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
                try {
                    for (StaticTreeNode node : nodes) {
                        String currentContent = Files.readString(node.getFile().toPath());
                        undoAction = undoAction.then(() -> Files.writeString(node.getFile().toPath(), currentContent));
                        redoAction = redoAction.then(() -> Files.writeString(node.getFile().toPath(),
                                currentContent.replaceAll(Pattern.quote(form.getReplaceText()), form.getWithText())));
                    }
                    MainWindow.getInstance().getActionManager().submit(undoAction, redoAction);
                    ReplaceInFilesModal.this.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(),
                            "Unknown error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        new FormButtonEnabledListener(replace.getModel(), form.getDocuments());
        buttonsPanel.add(replace);
        JButton cancel = new JButton(new Action("Cancel", "Cancel") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ReplaceInFilesModal.this.close();
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

    private static class ReplaceInFilesForm extends Form {
        private final JTextField replaceInput;
        private final JTextField withInput;

        private ReplaceInFilesForm() {
            replaceInput = new JTextField(50);
            withInput = new JTextField(50);

            addLabel(0, "Replace text");
            addInput(0, replaceInput);
            addLabel(1, "With");
            addInput(1, withInput);
        }

        public List<Document> getDocuments() {
            return List.of(replaceInput.getDocument(), withInput.getDocument());
        }

        public String getReplaceText() {
            return replaceInput.getText();
        }

        public String getWithText() {
            return withInput.getText();
        }
    }
}
