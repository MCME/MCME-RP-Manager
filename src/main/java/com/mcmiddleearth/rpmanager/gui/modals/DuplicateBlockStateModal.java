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

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.Form;
import com.mcmiddleearth.rpmanager.gui.components.Grid;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.FormButtonEnabledListener;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.Case;
import com.mcmiddleearth.rpmanager.model.Model;
import com.mcmiddleearth.rpmanager.utils.FileLoader;
import com.mcmiddleearth.rpmanager.utils.Pair;
import com.mcmiddleearth.rpmanager.utils.Triple;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuplicateBlockStateModal extends JDialog {
    private final JTree tree;
    private final StaticTreeNode node;
    private final BlockState blockState;

    public DuplicateBlockStateModal(Frame parent, JTree tree, StaticTreeNode node) throws IOException {
        super(parent, "Select files to duplicate", true);
        this.tree = tree;
        this.node = node;
        this.blockState = (BlockState) FileLoader.load(node.getFile()).getData();

        setLayout(new BorderLayout());
        VerticalBox verticalBox = new VerticalBox();
        DuplicateBlockStateForm form = new DuplicateBlockStateForm();
        verticalBox.add(form);
        DuplicateBlockStateGrid grid = new DuplicateBlockStateGrid();
        grid.setMinimumSize(new Dimension(300, 0));
        grid.setPreferredSize(new Dimension(300, (int) grid.getPreferredSize().getHeight()));
        grid.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        JScrollPane scrollPane = new FastScrollPane(grid);
        scrollPane.setMaximumSize(new Dimension(350, 0));
        scrollPane.setPreferredSize(new Dimension(350, 450));
        scrollPane.setMaximumSize(new Dimension(350, 450));
        verticalBox.add(scrollPane);
        add(verticalBox, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton next = new JButton(new Action("Next", "Next step") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                nextStep(form.getNewName(), grid.getReplacements());
                DuplicateBlockStateModal.this.close();
            }
        });
        new FormButtonEnabledListener(next.getModel(),
                Stream.concat(form.getDocuments().stream(), grid.getDocuments().stream()).toList());
        buttonsPanel.add(next);
        JButton cancel = new JButton(new Action("Cancel", "Cancel") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DuplicateBlockStateModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    private Set<String> getModelNames() {
        Set<String> modelNames = new LinkedHashSet<>();
        if (blockState.getVariants() != null) {
            for (List<Model> models : blockState.getVariants().values()) {
                modelNames.addAll(getModelNames(models));
            }
        } else if (blockState.getMultipart() != null) {
            for (Case c : blockState.getMultipart()) {
                modelNames.addAll(getModelNames(c.getApply()));
            }
        }
        return modelNames;
    }

    private Set<String> getModelNames(List<Model> models) {
        if (models != null) {
            return models.stream().map(model -> {
                String modelName = model.getModel();
                if (modelName.startsWith("minecraft:")) {
                    modelName = modelName.substring(10);
                }
                if (!modelName.contains("/")) {
                    modelName = "block/" + modelName;
                }
                return modelName;
            }).collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private void nextStep(String newName, List<Triple<String, StaticTreeNode, String>> replacements) {
        List<Pair<StaticTreeNode, String>> models = new LinkedList<>();
        for (Triple<String, StaticTreeNode, String> replacement : replacements) {
            models.add(new Pair<>(replacement.getMiddle(), replacement.getRight()));
            replaceModel(blockState, replacement.getLeft(), replacement.getRight());
        }
        try {
            new DuplicateBlockStateStep2Modal((Frame) getParent(), tree, (StaticTreeNode) node.getParent().getParent(),
                    newName, blockState, models);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getParent(), "Unknown error!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private StaticTreeNode getModelNode(String model) {
        StaticTreeNode baseNode = (StaticTreeNode) node.getParent().getParent();
        StaticTreeNode modelNode = baseNode.getChildren().stream()
                .filter(c -> "models".equals(c.getName())).findFirst().orElseThrow();
        for (String child : (model + ".json").split("/")) {
            modelNode = modelNode.getChildren().stream().filter(s -> child.equals(s.getName())).findFirst()
                    .orElse(null);
            if (modelNode == null) {
                return null;
            }
        }
        return modelNode;
    }

    private static void replaceModel(BlockState blockState, String oldName, String newName) {
        if (blockState.getVariants() != null) {
            for (List<Model> models : blockState.getVariants().values()) {
                replaceModel(models, oldName, newName);
            }
        } else if (blockState.getMultipart() != null) {
            for (Case c : blockState.getMultipart()) {
                replaceModel(c.getApply(), oldName, newName);
            }
        }
    }

    private static void replaceModel(List<Model> models, String oldName, String newName) {
        if (models != null) {
            for (Model model : models) {
                String currentName = model.getModel();
                if (currentName.startsWith("minecraft:")) {
                    currentName = currentName.substring(10);
                }
                if (!currentName.contains("/")) {
                    currentName = "block/" + currentName;
                }
                if (currentName.equals(oldName)) {
                    model.setModel(newName);
                }
            }
        }
    }

    private class DuplicateBlockStateForm extends Form {
        private final JTextField newNameInput;

        private DuplicateBlockStateForm() {
            newNameInput = new JTextField(50);
            newNameInput.setText(node.getName().replaceAll("\\.json$", ""));

            addLabel(0, "New name");
            addInput(0, newNameInput);
        }

        public List<Document> getDocuments() {
            return Collections.singletonList(newNameInput.getDocument());
        }

        public String getNewName() {
            return newNameInput.getText();
        }
    }

    private class DuplicateBlockStateGrid extends Grid {
        private final List<Supplier<Document>> documents = new LinkedList<>();
        private final List<Pair<String, Supplier<Pair<StaticTreeNode, String>>>> replacements = new LinkedList<>();
        private final List<JCheckBox> checkBoxes = new LinkedList<>();

        private DuplicateBlockStateGrid() {
            JCheckBox selectAllCheckBox = new JCheckBox("Copy?", null, false);
            selectAllCheckBox.addItemListener(event -> {
                setAllSelected(selectAllCheckBox.isSelected());
            });
            selectAllCheckBox.setToolTipText("Select/unselect all");
            addLabel(0, 0, selectAllCheckBox);
            addLabel(1, 0, "Old name");
            addLabel(2, 0, "New name");
            int y = 1;
            for (String modelName : getModelNames()) {
                StaticTreeNode node = getModelNode(modelName);
                JCheckBox checkBox = new JCheckBox(null, null, false);
                JTextField textField = new JTextField(modelName, 50);
                if (node != null) {
                    documents.add(() -> checkBox.isSelected() ? textField.getDocument() : null);
                    replacements.add(new Pair<>(modelName, () -> checkBox.isSelected() ?
                            new Pair<>(node, textField.getText()) : null));
                    checkBoxes.add(checkBox);
                } else {
                    checkBox.setEnabled(false);
                    textField.setEditable(false);
                }
                addLabel(0, y, checkBox);
                addLabel(1, y, modelName);
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
