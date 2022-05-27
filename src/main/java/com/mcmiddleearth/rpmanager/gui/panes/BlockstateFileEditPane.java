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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.modals.AddBlockstateModal;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.Model;
import com.mcmiddleearth.rpmanager.utils.BlockStateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlockstateFileEditPane extends VerticalBox {
    private final String fileName;
    private final BlockState blockState;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public BlockstateFileEditPane(String fileName, BlockState blockState) {
        this.fileName = fileName;
        this.blockState = blockState;

        if (!blockState.getVariants().isEmpty()) {
            JButton addSection = new JButton(new Action("Add section", "Add new block state section") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    addSection();
                }
            });
            this.add(addSection);
            boolean collapsed = blockState.getVariants().size() != 1;
            for (Map.Entry<String, List<Model>> variant : blockState.getVariants().entrySet()) {
                VariantEditPane variantEditPane = new VariantEditPane(variant.getKey(), variant.getValue());
                variantEditPane.addChangeListener(event -> onChange());
                this.add(new CollapsibleSection(variant.getKey(), variantEditPane, collapsed,
                        removeSectionButton(variant.getKey())));
                this.add(new JSeparator());
            }
        } else {
            //TODO
        }
    }

    private void addSection() {
        Map<String, List<String>> possibleStates = BlockStateUtils.getPossibleStates(fileName);
        if (possibleStates == null || possibleStates.isEmpty()) {
            doAddSection("");
        } else {
            new AddBlockstateModal(MainWindow.getInstance(), possibleStates, new Action("Accept", "Accept") {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    doAddSection(actionEvent.getActionCommand());
                }
            });
        }
    }

    private void doAddSection(String key) {
        List<Model> models = new LinkedList<>();
        models.add(new Model());
        doAddSection(key, models);
    }

    private void doAddSection(String key, List<Model> models) {
        blockState.getVariants().put(key, models);
        VariantEditPane variantEditPane = new VariantEditPane(key, models);
        variantEditPane.addChangeListener(event -> onChange());
        add(new CollapsibleSection(key, variantEditPane, false, removeSectionButton(key)));
        add(new JSeparator());
        revalidate();
        repaint();
        onChange();
    }

    private JButton removeSectionButton(String key) {
        return new JButton(new Action("-", "Remove section") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doRemoveSection(key);
            }
        });
    }

    private void doRemoveSection(String key) {
        blockState.getVariants().remove(key);
        for (int i = getComponentCount() - 1; i >= 0; --i) {
            Component component = getComponent(i);
            if (component instanceof CollapsibleSection section &&
                    section.getContent() instanceof VariantEditPane variantEditPane &&
                    variantEditPane.getVariant().equals(key)) {
                remove(i+1);
                remove(i);
            }
        }
        revalidate();
        repaint();
        onChange();
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, blockState));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
