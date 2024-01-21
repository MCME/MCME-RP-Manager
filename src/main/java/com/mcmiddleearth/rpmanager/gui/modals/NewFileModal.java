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
import com.mcmiddleearth.rpmanager.utils.loaders.BlockModelFileLoader;
import com.mcmiddleearth.rpmanager.utils.loaders.BlockstateFileLoader;
import com.mcmiddleearth.rpmanager.utils.loaders.ItemModelFileLoader;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class NewFileModal extends JDialog {
    private static final String NEW_BLOCKSTATE_CONTENT = """
            {
              "variants": {
              }
            }""";
    private static final String NEW_MODEL_CONTENT = """
            {
                "parent": "block/cube_all",
                "textures": {
                }
            }""";

    public NewFileModal(Frame parent, JTree tree, StaticTreeNode node) {
        super(parent, "New file", true);

        setLayout(new BorderLayout());
        NewFileForm form = new NewFileForm();
        add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton create = new JButton(new Action("Create file", "Create file") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!isValidName(form.getFileName())) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), "File name is invalid", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                File newFile = new File(node.getFile(), form.getFileName());
                if (newFile.exists()) {
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), "File already exists", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StaticTreeNode newNode =
                        new StaticTreeNode(node, form.getFileName(), newFile, false, new LinkedList<>());
                newNode.setGit(node.getGit());
                node.addChild(newNode);
                com.mcmiddleearth.rpmanager.utils.Action redoAction = newFile::createNewFile;
                redoAction = redoAction.then(() -> {
                    String content = "";
                    if (isBlockstate(newNode)) {
                        content = NEW_BLOCKSTATE_CONTENT;
                    } else if (isModel(newNode)) {
                        content = NEW_MODEL_CONTENT;
                    }
                    if (!content.isEmpty()) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                });
                MainWindow.getInstance().getActionManager().submit(newFile::delete, redoAction);
                try {
                    node.refreshGitStatus();
                } catch (GitAPIException e) {
                    //TODO error dialog?
                }
                ((DefaultTreeModel) tree.getModel()).reload(node);
                TreePath path = TreeUtils.getPathForNode(newNode);
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
                NewFileModal.this.close();
            }
        });
        new FormButtonEnabledListener(create.getModel(), form.getDocuments());
        buttonsPanel.add(create);
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating file") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NewFileModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    private static boolean isBlockstate(StaticTreeNode node) {
        return new BlockstateFileLoader().canLoad(node.getFile());
    }

    private static boolean isModel(StaticTreeNode node) {
        return new BlockModelFileLoader().canLoad(node.getFile()) || new ItemModelFileLoader().canLoad(node.getFile());
    }

    private static boolean isValidName(String name) {
        return Stream.of(File.separator, "\"", "*", "<", ">", "?", "|").noneMatch(name::contains);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class NewFileForm extends Form {
        private final JTextField nameInput;

        private NewFileForm() {
            nameInput = new JTextField(50);

            addLabel(0, "File name");
            addInput(0, nameInput);
        }

        public List<Document> getDocuments() {
            return List.of(nameInput.getDocument());
        }

        public String getFileName() {
            return nameInput.getText();
        }
    }
}
