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
import com.mcmiddleearth.rpmanager.gui.components.Grid;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.FormButtonEnabledListener;
import com.mcmiddleearth.rpmanager.model.BaseModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.utils.FileLoader;
import com.mcmiddleearth.rpmanager.utils.Pair;
import com.mcmiddleearth.rpmanager.utils.ResourcePackUtils;
import com.mcmiddleearth.rpmanager.utils.Triple;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DuplicateBlockStateStep2Modal extends JDialog {
    private final JTree tree;
    private final StaticTreeNode baseNode;
    private final String newName;
    private final BlockState blockState;
    private final List<Triple<StaticTreeNode, BaseModel, String>> replacements = new LinkedList<>();

    public DuplicateBlockStateStep2Modal(Frame parent, JTree tree, StaticTreeNode baseNode, String newName,
                                         BlockState blockState, List<Pair<StaticTreeNode, String>> replacements)
            throws IOException {
        super(parent, "Select textures to duplicate");
        this.tree = tree;
        this.baseNode = baseNode;
        this.newName = newName;
        this.blockState = blockState;
        for (Pair<StaticTreeNode, String> replacement : replacements) {
            this.replacements.add(new Triple<>(replacement.getLeft(),
                    (BaseModel) FileLoader.load(replacement.getLeft().getFile()).getData(),
                    replacement.getRight()));
        }

        setLayout(new BorderLayout());
        DuplicateBlockStateStep2Grid grid = new DuplicateBlockStateStep2Grid();
        add(grid, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton apply = new JButton(new Action("Apply", "Apply changes") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                applyChanges(grid.getReplacements());
                DuplicateBlockStateStep2Modal.this.close();
            }
        });
        new FormButtonEnabledListener(apply.getModel(), grid.getDocuments());
        buttonsPanel.add(apply);
        JButton cancel = new JButton(new Action("Cancel", "Cancel") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DuplicateBlockStateStep2Modal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    private Set<String> getTextureNames() {
        Set<String> textureNames = new LinkedHashSet<>();
        replacements.forEach(replacement -> textureNames.addAll(getTextureNames(replacement.getMiddle())));
        return textureNames;
    }

    private Set<String> getTextureNames(BaseModel baseModel) {
        return baseModel.getTextures().values().stream().map(s -> s.startsWith("minecraft:") ? s.substring(10) : s)
                .collect(Collectors.toSet());
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private void applyChanges(List<Triple<String, StaticTreeNode, String>> replacements) {
        for (Triple<StaticTreeNode, BaseModel, String> model : this.replacements) {
            replaceTextures(model.getMiddle(), replacements);
        }
        try {
            com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
            com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
            Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> action =
                    writeTextures(replacements);
            undoAction = undoAction.butFirst(action.getLeft());
            redoAction = redoAction.then(action.getRight());
            action = writeModels(this.replacements);
            undoAction = undoAction.butFirst(action.getLeft());
            redoAction = redoAction.then(action.getRight());
            action = writeBlockState(this.blockState, newName + ".json");
            undoAction = undoAction.butFirst(action.getLeft());
            redoAction = redoAction.then(action.getRight());
            MainWindow.getInstance().getActionManager().submit(undoAction, redoAction);
            baseNode.refreshGitStatus();
        } catch (IOException | GitAPIException e) {
            JOptionPane.showMessageDialog(getParent(), "Unknown error!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        ((DefaultTreeModel) tree.getModel()).reload(baseNode);
    }

    private StaticTreeNode getTextureNode(String textureName) {
        StaticTreeNode textureNode = baseNode.getChildren().stream().filter(s -> "textures".equals(s.getName()))
                .findFirst().orElseThrow();
        for (String child : (textureName + ".png").split("/")) {
            textureNode = textureNode.getChildren().stream().filter(s -> child.equals(s.getName())).findFirst()
                    .orElse(null);
            if (textureNode == null) {
                return null;
            }
        }
        return textureNode;
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeTextures(
            List<Triple<String, StaticTreeNode, String>> replacements) throws IOException {
        StaticTreeNode texturesNode = baseNode.getChildren().stream().filter(s -> "textures".equals(s.getName()))
                .findFirst().orElseThrow();
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        for (Triple<String, StaticTreeNode, String> replacement : replacements) {
            Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> action =
                    writeTexture(texturesNode, replacement.getMiddle().getFile(), replacement.getRight());
            undoAction = undoAction.butFirst(action.getLeft());
            redoAction = redoAction.then(action.getRight());
        }
        return new Pair<>(undoAction, redoAction);
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeTexture(
            StaticTreeNode texturesNode, File oldFile, String newName) throws IOException {
        String[] newPath = (newName + ".png").split("/");
        StaticTreeNode newNode = texturesNode;
        for (int i = 0; i < newPath.length; ++i) {
            String part = newPath[i];
            StaticTreeNode child = newNode.getChildren().stream().filter(s -> part.equals(s.getName())).findFirst()
                    .orElse(null);
            if (child == null) {
                child = new StaticTreeNode(newNode, part, new File(newNode.getFile(), part),
                        i != newPath.length - 1, new LinkedList<>());
                newNode.addChild(child);
            }
            newNode = child;
        }
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        File newFile = newNode.getFile();
        for (File f = newFile.getParentFile(); !f.exists(); f = f.getParentFile()) {
            File finalFile = f;
            undoAction = undoAction.then(() -> finalFile.delete());
        }
        redoAction = redoAction.then(() -> newFile.getParentFile().mkdirs());
        undoAction = undoAction.butFirst(() -> newFile.delete());
        if (newFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(newFile)) {
                byte[] content = inputStream.readAllBytes();
                undoAction = undoAction.then(() -> {
                    try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                        outputStream.write(content);
                    }
                });
            }
        }
        redoAction = redoAction.then(() -> {
            try (FileInputStream input = new FileInputStream(oldFile);
                 FileOutputStream output = new FileOutputStream(newFile)) {
                input.transferTo(output);
            }
        });
        return new Pair<>(undoAction, redoAction);
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeModels(
            List<Triple<StaticTreeNode, BaseModel, String>> replacements) throws IOException {
        StaticTreeNode modelsNode = baseNode.getChildren().stream().filter(s -> "models".equals(s.getName()))
                .findFirst().orElseThrow();
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        for (Triple<StaticTreeNode, BaseModel, String> replacement : replacements) {
            Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> action =
                    writeModel(modelsNode, replacement.getMiddle(), replacement.getRight());
            undoAction = undoAction.butFirst(action.getLeft());
            redoAction = redoAction.then(action.getRight());
        }
        return new Pair<>(undoAction, redoAction);
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeModel(
            StaticTreeNode modelsNode, BaseModel data, String name) throws IOException {
        String[] newPath = (name + ".json").split("/");
        StaticTreeNode newNode = modelsNode;
        for (int i = 0; i < newPath.length; ++i) {
            String part = newPath[i];
            StaticTreeNode child = newNode.getChildren().stream().filter(s -> part.equals(s.getName())).findFirst()
                    .orElse(null);
            if (child == null) {
                child = new StaticTreeNode(newNode, part, new File(newNode.getFile(), part),
                        i != newPath.length - 1, new LinkedList<>());
                newNode.addChild(child);
            }
            newNode = child;
        }
        File newFile = newNode.getFile();
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        for (File f = newFile.getParentFile(); !f.exists(); f = f.getParentFile()) {
            File finalFile = f;
            undoAction = undoAction.then(() -> finalFile.delete());
        }
        redoAction = redoAction.then(() -> newFile.getParentFile().mkdirs());
        undoAction = undoAction.butFirst(() -> newFile.delete());
        if (newFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(newFile)) {
                byte[] content = inputStream.readAllBytes();
                undoAction = undoAction.then(() -> {
                    try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                        outputStream.write(content);
                    }
                });
            }
        }
        redoAction = redoAction.then(() -> ResourcePackUtils.saveFile(data, newFile));
        return new Pair<>(undoAction, redoAction);
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeBlockState(
            BlockState data, String name) throws IOException {
        StaticTreeNode newNode = baseNode.getChildren().stream().filter(s -> "blockstates".equals(s.getName()))
                .findFirst().orElseThrow();
        StaticTreeNode child = newNode.getChildren().stream().filter(s -> name.equals(s.getName())).findFirst()
                .orElse(null);
        if (child == null) {
            child = new StaticTreeNode(newNode, name, new File(newNode.getFile(), name), false, new LinkedList<>());
            newNode.addChild(child);
        }
        newNode = child;
        File newFile = newNode.getFile();
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        undoAction = undoAction.butFirst(() -> newFile.delete());
        if (newFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(newFile)) {
                byte[] content = inputStream.readAllBytes();
                undoAction = undoAction.then(() -> {
                    try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                        outputStream.write(content);
                    }
                });
            }
        }
        return new Pair<>(undoAction, () -> ResourcePackUtils.saveFile(data, newFile));
    }

    private void replaceTextures(BaseModel model, List<Triple<String, StaticTreeNode, String>> replacements) {
        replacements.forEach(r -> replaceTextures(model, r.getLeft(), r.getRight()));
    }

    private void replaceTextures(BaseModel model, String oldName, String newName) {
        for (String key : model.getTextures().keySet()) {
            if (oldName.equals(model.getTextures().get(key))) {
                model.getTextures().put(key, newName);
            } else if (oldName.equals("minecraft:" + model.getTextures().get(key))) {
                model.getTextures().put(key, newName);
            }
        }
    }

    private class DuplicateBlockStateStep2Grid extends Grid {
        private final List<Supplier<Document>> documents = new LinkedList<>();
        private final List<Pair<String, Supplier<Pair<StaticTreeNode, String>>>> replacements = new LinkedList<>();
        private final List<JCheckBox> checkBoxes = new LinkedList<>();

        private DuplicateBlockStateStep2Grid() {
            JCheckBox selectAllCheckBox = new JCheckBox("Copy?", null, false);
            selectAllCheckBox.addItemListener(event -> {
                setAllSelected(selectAllCheckBox.isSelected());
            });
            selectAllCheckBox.setToolTipText("Select/unselect all");
            addLabel(0, 0, selectAllCheckBox);
            addLabel(1, 0, "Old name");
            addLabel(2, 0, "New name");
            int y = 1;
            for (String textureName : getTextureNames()) {
                StaticTreeNode node = getTextureNode(textureName);
                JCheckBox checkBox = new JCheckBox(null, null, false);
                JTextField textField = new JTextField(textureName, 50);
                if (node != null) {
                    documents.add(() -> checkBox.isSelected() ? textField.getDocument() : null);
                    replacements.add(new Pair<>(textureName, () -> checkBox.isSelected() ?
                            new Pair<>(node, textField.getText()) : null));
                    checkBoxes.add(checkBox);
                } else {
                    checkBox.setEnabled(false);
                    textField.setEditable(false);
                }
                addLabel(0, y, checkBox);
                addLabel(1, y, textureName);
                addInput(2, y, textField);
                y++;
            }
        }

        public void setAllSelected(boolean selected) {
            checkBoxes.forEach(cb -> cb.setSelected(selected));
        }

        public List<Document> getDocuments() {
            return documents.stream().map(Supplier::get).filter(Objects::nonNull).toList();
        }

        public List<Triple<String, StaticTreeNode, String>> getReplacements() {
            return replacements.stream()
                    .filter(r -> r.getRight().get() != null)
                    .map(r -> new Triple<>(r.getLeft(), r.getRight().get().getLeft(), r.getRight().get().getRight()))
                    .filter(p -> p.getRight() != null).toList();
        }
    }
}
