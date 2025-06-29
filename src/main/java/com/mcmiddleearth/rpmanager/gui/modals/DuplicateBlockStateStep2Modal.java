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
import com.mcmiddleearth.rpmanager.model.internal.NamespacedPath;
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
    private final List<Triple<StaticTreeNode, BaseModel, NamespacedPath>> replacements = new LinkedList<>();

    public DuplicateBlockStateStep2Modal(Frame parent, JTree tree, StaticTreeNode baseNode, String newName,
                                         BlockState blockState, List<Pair<StaticTreeNode, NamespacedPath>> replacements)
            throws IOException {
        super(parent, "Select textures to duplicate");
        this.tree = tree;
        this.baseNode = baseNode;
        this.newName = newName;
        this.blockState = blockState;
        for (Pair<StaticTreeNode, NamespacedPath> replacement : replacements) {
            this.replacements.add(new Triple<>(replacement.getLeft(),
                    (BaseModel) FileLoader.load(replacement.getLeft()).getData(),
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

    private Set<NamespacedPath> getTextureNames() {
        Set<NamespacedPath> textureNames = new LinkedHashSet<>();
        replacements.forEach(replacement -> textureNames.addAll(getTextureNames(replacement.getMiddle())));
        return textureNames;
    }

    private Set<NamespacedPath> getTextureNames(BaseModel baseModel) {
        return baseModel.getTextures().values().stream().map(ResourcePackUtils::extractPrefix)
                .collect(Collectors.toSet());
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private void applyChanges(List<Triple<NamespacedPath, StaticTreeNode, NamespacedPath>> replacements) {
        for (Triple<StaticTreeNode, BaseModel, NamespacedPath> model : this.replacements) {
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
            action = writeBlockState(this.blockState,
                    new NamespacedPath(ResourcePackUtils.DEFAULT_NAMESPACE, newName + ".json"));
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

    private StaticTreeNode getTextureNode(NamespacedPath textureName) {
        StaticTreeNode textureNode = baseNode.getChildren().stream()
                .filter(s -> textureName.namespace().equals(s.getName()))
                .findFirst().orElseThrow();
        for (String child : ("textures/" + textureName.path() + ".png").split("/")) {
            textureNode = textureNode.getChildren().stream().filter(s -> child.equals(s.getName())).findFirst()
                    .orElse(null);
            if (textureNode == null) {
                return null;
            }
        }
        return textureNode;
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeTextures(
            List<Triple<NamespacedPath, StaticTreeNode, NamespacedPath>> replacements) throws IOException {
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        for (Triple<NamespacedPath, StaticTreeNode, NamespacedPath> replacement : replacements) {
            StaticTreeNode namespaceNode = baseNode.getChildren().stream()
                    .filter(s -> replacement.getRight().namespace().equals(s.getName()))
                    .findFirst().orElse(null);
            if (namespaceNode == null) {
                namespaceNode = attachDirectoryNode(baseNode, replacement.getRight().namespace());
            }
            StaticTreeNode texturesNode = namespaceNode.getChildren().stream()
                    .filter(s -> "textures".equals(s.getName()))
                    .findFirst().orElse(null);
            if (texturesNode == null) {
                texturesNode = attachDirectoryNode(namespaceNode, "textures");
            }
            Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> action =
                    writeTexture(texturesNode, replacement.getMiddle().getFile(), replacement.getRight().path());
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
            List<Triple<StaticTreeNode, BaseModel, NamespacedPath>> replacements) throws IOException {
        com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
        com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
        for (Triple<StaticTreeNode, BaseModel, NamespacedPath> replacement : replacements) {
            StaticTreeNode namespaceNode = baseNode.getChildren().stream()
                    .filter(s -> replacement.getRight().namespace().equals(s.getName()))
                    .findFirst().orElse(null);
            if (namespaceNode == null) {
                namespaceNode = attachDirectoryNode(baseNode, replacement.getRight().namespace());
            }
            StaticTreeNode modelsNode = namespaceNode.getChildren().stream().filter(s -> "models".equals(s.getName()))
                    .findFirst().orElse(null);
            if (modelsNode == null) {
                modelsNode = attachDirectoryNode(namespaceNode, "models");
            }
            Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> action =
                    writeModel(modelsNode, replacement.getMiddle(), replacement.getRight().path());
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

    private StaticTreeNode attachDirectoryNode(StaticTreeNode parent, String name) {
        StaticTreeNode node = new StaticTreeNode(parent, name, new File(parent.getFile(), name), true,
                new LinkedList<>());
        parent.addChild(node);
        return node;
    }

    private Pair<com.mcmiddleearth.rpmanager.utils.Action, com.mcmiddleearth.rpmanager.utils.Action> writeBlockState(
            BlockState data, NamespacedPath name) throws IOException {
        StaticTreeNode namespaceNode = baseNode.getChildren().stream()
                .filter(s -> name.namespace().equals(s.getName()))
                .findFirst().orElse(null);
        if (namespaceNode == null) {
            namespaceNode = attachDirectoryNode(baseNode, name.namespace());
        }
        StaticTreeNode newNode = namespaceNode.getChildren().stream().filter(s -> "blockstates".equals(s.getName()))
                .findFirst().orElse(null);
        if (newNode == null) {
            newNode = attachDirectoryNode(namespaceNode, "blockstates");
        }
        StaticTreeNode child = newNode.getChildren().stream()
                .filter(s -> name.path().equals(s.getName())).findFirst()
                .orElse(null);
        if (child == null) {
            child = new StaticTreeNode(newNode, name.path(), new File(newNode.getFile(), name.path()), false,
                    new LinkedList<>());
            newNode.addChild(child);
        }
        newNode = child;
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

    private void replaceTextures(BaseModel model, List<Triple<NamespacedPath, StaticTreeNode, NamespacedPath>> replacements) {
        replacements.forEach(r -> replaceTextures(model, r.getLeft(), r.getRight()));
    }

    private void replaceTextures(BaseModel model, NamespacedPath oldName, NamespacedPath newName) {
        for (String key : model.getTextures().keySet()) {
            if (oldName.equals(ResourcePackUtils.extractPrefix(model.getTextures().get(key)))) {
                model.getTextures().put(key, newName.toString());
            }
        }
    }

    private class DuplicateBlockStateStep2Grid extends Grid {
        private final List<Supplier<Document>> documents = new LinkedList<>();
        private final List<Pair<NamespacedPath, Supplier<Pair<StaticTreeNode, NamespacedPath>>>> replacements =
                new LinkedList<>();
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
            for (NamespacedPath textureName : getTextureNames()) {
                StaticTreeNode node = getTextureNode(textureName);
                JCheckBox checkBox = new JCheckBox(null, null, false);
                JTextField textField = new JTextField(textureName.toString(), 50);
                if (node != null) {
                    documents.add(() -> checkBox.isSelected() ? textField.getDocument() : null);
                    replacements.add(new Pair<>(textureName, () -> checkBox.isSelected() ?
                            new Pair<>(node, ResourcePackUtils.extractPrefix(textField.getText())) : null));
                    checkBoxes.add(checkBox);
                } else {
                    checkBox.setEnabled(false);
                    textField.setEditable(false);
                }
                addLabel(0, y, checkBox);
                addLabel(1, y, textureName.toString());
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

        public List<Triple<NamespacedPath, StaticTreeNode, NamespacedPath>> getReplacements() {
            return replacements.stream()
                    .filter(r -> r.getRight().get() != null)
                    .map(r -> new Triple<>(r.getLeft(), r.getRight().get().getLeft(), r.getRight().get().getRight()))
                    .filter(p -> p.getRight() != null).toList();
        }
    }
}
