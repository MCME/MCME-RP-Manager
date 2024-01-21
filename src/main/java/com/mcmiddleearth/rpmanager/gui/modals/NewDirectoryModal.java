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
import com.mcmiddleearth.rpmanager.gui.utils.TreeUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class NewDirectoryModal extends JDialog {
    public NewDirectoryModal(Frame parent, JTree tree, StaticTreeNode node) {
        super(parent, "New directory", true);

        setLayout(new BorderLayout());
        NewDirectoryForm form = new NewDirectoryForm();
        add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton create = new JButton(new Action("Create directory", "Create directory") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!isValidName(form.getDirectoryName())) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), "File name is invalid", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                File newDirectory = new File(node.getFile(), form.getDirectoryName());
                if (newDirectory.exists()) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), "File already exists", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StaticTreeNode newNode =
                        new StaticTreeNode(node, form.getDirectoryName(), newDirectory, true, new LinkedList<>());
                newNode.setGit(node.getGit());
                node.addChild(newNode);
                MainWindow.getInstance().getActionManager().submit(newDirectory::delete, newDirectory::mkdir);
                try {
                    node.refreshGitStatus();
                } catch (GitAPIException e) {
                    //TODO error dialog?
                }
                ((DefaultTreeModel) tree.getModel()).reload(node);
                TreePath path = TreeUtils.getPathForNode(newNode);
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
                NewDirectoryModal.this.close();
            }
        });
        new FormButtonEnabledListener(create.getModel(), form.getDocuments());
        buttonsPanel.add(create);
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating directory") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NewDirectoryModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    private static boolean isValidName(String name) {
        return Stream.of(File.separator, "\"", "*", "<", ">", "?", "|").noneMatch(name::contains);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class NewDirectoryForm extends Form {
        private final JTextField nameInput;

        private NewDirectoryForm() {
            nameInput = new JTextField(50);

            addLabel(0, "Directory name");
            addInput(0, nameInput);
        }

        public java.util.List<Document> getDocuments() {
            return List.of(nameInput.getDocument());
        }

        public String getDirectoryName() {
            return nameInput.getText();
        }
    }
}
