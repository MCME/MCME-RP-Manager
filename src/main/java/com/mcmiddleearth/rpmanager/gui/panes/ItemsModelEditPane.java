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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.Event;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.*;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.gui.modals.ChooseItemsModelTypeModal;
import com.mcmiddleearth.rpmanager.model.internal.ItemsModelType;
import com.mcmiddleearth.rpmanager.model.item.*;
import com.mcmiddleearth.rpmanager.model.item.range.CustomModelDataRangeProperty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

public class ItemsModelEditPane extends VerticalBox {
    private ItemsModel itemsModel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ItemsModelEditPane(ItemsModel itemsModel) {
        this.itemsModel = itemsModel;

        buildContent();
    }

    private void buildContent() {
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
        typePanel.add(new JLabel("Type: "));
        typePanel.add(Box.createHorizontalGlue());
        typePanel.add(new JLabel(ItemsModelType.byItemsModel(itemsModel).getId()));
        typePanel.add(new IconButton(new Action("Edit", Icons.EDIT_ICON, "Change model type") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new ChooseItemsModelTypeModal(MainWindow.getInstance(), ItemsModelType.byItemsModel(itemsModel),
                        ItemsModelEditPane.this::onModelTypeChanged, true);
            }
        }));
        this.add(typePanel);

        if (itemsModel instanceof RangeDispatchItemsModel rangeDispatchItemsModel) {
            if (rangeDispatchItemsModel.getProperty() == null) {
                rangeDispatchItemsModel.setProperty(new CustomModelDataRangeProperty());
            }
            RangePropertyEditPane rangePropertyEditPane =
                    new RangePropertyEditPane(rangeDispatchItemsModel.getProperty());
            rangePropertyEditPane.addChangeListener(this::onChange);
            this.add(rangePropertyEditPane);

            JPanel scalePanel = new JPanel();
            scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.X_AXIS));
            scalePanel.add(new JLabel("Scale: "));
            scalePanel.add(new NumberInput(rangeDispatchItemsModel.getScale(),
                    v -> rangeDispatchItemsModel.setScale(Float.valueOf(1f).equals(v) ? null : v),
                    input -> onChange(new ChangeEvent(input, input.getText()))));
            this.add(scalePanel);

            VerticalBox entriesBox = new VerticalBox();
            if (rangeDispatchItemsModel.getEntries() == null) {
                rangeDispatchItemsModel.setEntries(new LinkedList<>());
            }
            boolean collapsed = rangeDispatchItemsModel.getEntries().size() != 1;
            for (RangeDispatchItemsModel.Entry entry : rangeDispatchItemsModel.getEntries()) {
                RangeDispatchItemsModelEntryEditPane entryEditPane = new RangeDispatchItemsModelEntryEditPane(entry);
                entryEditPane.addChangeListener(this::onChange);
                entriesBox.add(new CollapsibleSection("Entry", entryEditPane, collapsed,
                        removeEntryButton(entry, entriesBox)));
                entriesBox.add(new JSeparator());
            }
            this.add(new CollapsibleSection("Entries", entriesBox, collapsed, addEntryButton(entriesBox)));

            this.add(new JSeparator());
            JLabel fallbackLabel = new JLabel("Fallback");
            fallbackLabel.setFont(fallbackLabel.getFont().deriveFont(Font.BOLD));
            this.add(fallbackLabel);
            if (rangeDispatchItemsModel.getFallback() == null) {
                rangeDispatchItemsModel.setFallback(new EmptyItemsModel());
            }
            ItemsModelEditPane fallbackEditPane = new ItemsModelEditPane(rangeDispatchItemsModel.getFallback());
            fallbackEditPane.addChangeListener(this::onChange);
            fallbackEditPane.addModelTypeChangeListener(
                    e -> rangeDispatchItemsModel.setFallback(e.getNewValue()));
            this.add(fallbackEditPane);
        } else if (itemsModel instanceof ModelItemsModel modelItemsModel) {
            JPanel modelPanel = new JPanel();
            modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.X_AXIS));
            modelPanel.add(new JLabel("Model: "));
            modelPanel.add(new TextInput(modelItemsModel.getModel(),
                    modelItemsModel::setModel,
                    input -> onChange(new ChangeEvent(input, input.getText()))));
            this.add(modelPanel);

            this.add(new JLabel("Tints are not supported."));
        } else if (itemsModel instanceof EmptyItemsModel || itemsModel instanceof BundleSelectedItemItemsModel) {
            this.add(new JLabel("No additional properties."));
        } else {
            this.add(new JLabel("Model type not supported."));
        }
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    public void addModelTypeChangeListener(EventListener<ModelTypeChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ModelTypeChangeEvent.class);
    }

    private JButton removeEntryButton(RangeDispatchItemsModel.Entry entry, VerticalBox entriesBox) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove entry") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doRemoveEntry(entry, entriesBox);
            }
        });
    }

    private void doRemoveEntry(RangeDispatchItemsModel.Entry entry, VerticalBox entriesBox) {
        ((RangeDispatchItemsModel) itemsModel).getEntries().remove(entry);
        for (int i = entriesBox.getComponentCount() - 1; i >= 0; --i) {
            Component component = entriesBox.getComponent(i);
            if (component instanceof CollapsibleSection section &&
                    section.getContent() instanceof RangeDispatchItemsModelEntryEditPane entryEditPane &&
                    entryEditPane.getEntry() == entry) {
                remove(i+1);
                remove(i);
            }
        }
        revalidate();
        repaint();
        onChange(new ChangeEvent(entriesBox, null));
    }

    private JButton addEntryButton(VerticalBox entriesBox) {
        return new IconButton(new Action("+", Icons.ADD_ICON, "Add entry") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doAddEntry(entriesBox);
            }
        });
    }

    private void doAddEntry(VerticalBox entriesBox) {
        RangeDispatchItemsModel.Entry entry = new RangeDispatchItemsModel.Entry();
        entry.setModel(new EmptyItemsModel());
        ((RangeDispatchItemsModel) itemsModel).getEntries().add(entry);
        RangeDispatchItemsModelEntryEditPane entryEditPane = new RangeDispatchItemsModelEntryEditPane(entry);
        entryEditPane.addChangeListener(this::onChange);
        entriesBox.add(new CollapsibleSection("Entry", entryEditPane, false,
                removeEntryButton(entry, entriesBox)));
        entriesBox.add(new JSeparator());
        revalidate();
        repaint();
        onChange(new ChangeEvent(entriesBox, null));
    }

    private void onModelTypeChanged(ItemsModelType newType) {
        try {
            itemsModel = newType.getTypeClass().getConstructor().newInstance();
            eventDispatcher.dispatchEvent(new ModelTypeChangeEvent(this, itemsModel));
            onChange(new ChangeEvent(this, itemsModel));
            removeAll();
            buildContent();
            revalidate();
            repaint();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException
                 | NoSuchMethodException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(), "Failed to change model type", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class ModelTypeChangeEvent implements Event {
        private final Object source;
        private final ItemsModel newValue;

        public ModelTypeChangeEvent(Object source, ItemsModel newValue) {
            this.source = source;
            this.newValue = newValue;
        }

        @Override
        public Object getSource() {
            return source;
        }

        public ItemsModel getNewValue() {
            return newValue;
        }
    }
}
