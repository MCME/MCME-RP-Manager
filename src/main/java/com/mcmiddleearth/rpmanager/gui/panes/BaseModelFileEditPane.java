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
import com.mcmiddleearth.rpmanager.model.BaseModel;
import com.mcmiddleearth.rpmanager.model.BlockModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class BaseModelFileEditPane extends VerticalBox {
    private final BaseModel baseModel;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    protected final JPanel parentPanel;
    protected final JPanel displayPanel;
    protected final JPanel texturesPanel;
    protected final JPanel elementsPanel;

    public BaseModelFileEditPane(BaseModel baseModel) {
        this.baseModel = baseModel;

        this.parentPanel = new JPanel();
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.X_AXIS));
        parentPanel.add(new JLabel("Parent: "));
        TextInput parentInput = new TextInput(baseModel.getParent(), baseModel::setParent, i -> onChange());
        parentPanel.add(parentInput);

        ModelDisplayEditPane displayEditPane = new ModelDisplayEditPane(baseModel);
        displayEditPane.addChangeListener(e -> onChange());
        CollapsibleSection displayEditSection =
                new CollapsibleSection("display: {", displayEditPane, false,
                        new JButton(new Action("+", "Add display entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                displayEditPane.addDisplayEntry();
                            }
                        }));
        VerticalBox displayBox = new VerticalBox();
        displayBox.add(displayEditSection);
        displayBox.add(new JLabel("}"));
        this.displayPanel = displayBox;

        ModelTexturesEditPane texturesEditPane = new ModelTexturesEditPane(baseModel);
        texturesEditPane.addChangeListener(e -> onChange());
        CollapsibleSection texturesEditSection =
                new CollapsibleSection("textures: {", texturesEditPane, false,
                        new JButton(new Action("+", "Add texture entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                texturesEditPane.addTextureEntry();
                            }
                        }));
        VerticalBox texturesBox = new VerticalBox();
        texturesBox.add(texturesEditSection);
        texturesBox.add(new JLabel("}"));
        this.texturesPanel = texturesBox;

        ModelElementsEditPane elementsEditPane = new ModelElementsEditPane(baseModel);
        elementsEditPane.addChangeListener(e -> onChange());
        CollapsibleSection elementsEditSection =
                new CollapsibleSection("elements: {", elementsEditPane, false,
                        new JButton(new Action("+", "Add element entry") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                elementsEditPane.addElementEntry();
                            }
                        }));
        VerticalBox elementsBox = new VerticalBox();
        elementsBox.add(elementsEditSection);
        elementsBox.add(new JLabel("}"));
        this.elementsPanel = elementsBox;
    }

    protected void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, baseModel));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
