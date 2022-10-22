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
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.TextInput;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.BlockModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BlockModelFileEditPane extends VerticalBox {
    private final String fileName;
    private final BlockModel blockModel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public BlockModelFileEditPane(String fileName, BlockModel blockModel) {
        this.fileName = fileName;
        this.blockModel = blockModel;

        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.X_AXIS));
        parentPanel.add(new JLabel("Parent: "));
        TextInput parentInput = new TextInput(blockModel.getParent(), blockModel::setParent, i -> onChange());
        parentPanel.add(parentInput);
        this.add(parentPanel);

        JCheckBox ambientOcclusionInput = new JCheckBox(
                "Ambient occlusion", Boolean.TRUE.equals(blockModel.getAmbientocclusion()));
        ambientOcclusionInput.addItemListener(event -> {
            blockModel.setAmbientocclusion(ambientOcclusionInput.isSelected() ? true : null);
            onChange();
        });
        this.add(ambientOcclusionInput);

        ModelDisplayEditPane displayEditPane = new ModelDisplayEditPane(blockModel);
        displayEditPane.addChangeListener(e -> onChange());
        CollapsibleSection displayEditSection =
                new CollapsibleSection("display: {", displayEditPane, false,
                        new JButton(new Action("+", "Add display entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                displayEditPane.addDisplayEntry();
                            }
                        }));
        this.add(displayEditSection);
        this.add(new JLabel("}"));

        ModelTexturesEditPane texturesEditPane = new ModelTexturesEditPane(blockModel);
        texturesEditPane.addChangeListener(e -> onChange());
        CollapsibleSection texturesEditSection =
                new CollapsibleSection("textures: {", texturesEditPane, false,
                        new JButton(new Action("+", "Add texture entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                texturesEditPane.addTextureEntry();
                            }
                        }));
        this.add(texturesEditSection);
        this.add(new JLabel("}"));

        ModelElementsEditPane elementsEditPane = new ModelElementsEditPane(blockModel);
        elementsEditPane.addChangeListener(e -> onChange());
        CollapsibleSection elementsEditSection =
                new CollapsibleSection("elements: {", elementsEditPane, false,
                        new JButton(new Action("+", "Add element entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                elementsEditPane.addElementEntry();
                            }
                        }));
        this.add(elementsEditSection);
        this.add(new JLabel("}"));
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, blockModel));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
