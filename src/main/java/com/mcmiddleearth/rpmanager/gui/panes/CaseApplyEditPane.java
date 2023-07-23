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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.IconButton;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.model.Case;
import com.mcmiddleearth.rpmanager.model.Model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;

public class CaseApplyEditPane extends VerticalBox {
    private final Case theCase;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public CaseApplyEditPane(Case theCase) {
        this.theCase = theCase;

        setBorder(new EmptyBorder(0, 10, 0, 0));

        this.add(new JButton(new Action("Add model", "Add new model to this case") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addModel();
            }
        }));

        boolean collapsed = theCase.getApply().size() != 1;
        int label = 1;
        for (Model model : theCase.getApply()) {
            VariantModelEditPane variantModelEditPane = new VariantModelEditPane(model);
            variantModelEditPane.addChangeListener(this::onChange);
            this.add(new CollapsibleSection(
                    Integer.toString(label++), variantModelEditPane, collapsed, removeModelButton(model)));
            this.add(new JSeparator());
        }
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    private JButton removeModelButton(Model model) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove model") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeModel(model);
            }
        });
    }

    private void addModel() {
        Model model = new Model();
        theCase.getApply().add(model);

        VariantModelEditPane variantModelEditPane = new VariantModelEditPane(model);
        variantModelEditPane.addChangeListener(this::onChange);
        this.add(new CollapsibleSection(
                Integer.toString(theCase.getApply().size()), variantModelEditPane, false, removeModelButton(model)));
        this.add(new JSeparator());

        revalidate();
        repaint();

        eventDispatcher.dispatchEvent(new ChangeEvent(this, model));
    }

    private void removeModel(Model model) {
        for (int i = getComponentCount() - 1; i >= 0; --i) {
            if (getComponent(i) instanceof CollapsibleSection section &&
                    section.getContent() instanceof VariantModelEditPane variantModelEditPane &&
                    variantModelEditPane.getModel() == model) {
                remove(i+1);
                remove(i);
            }
        }

        theCase.getApply().remove(model);
        updateLabels();

        revalidate();
        repaint();

        eventDispatcher.dispatchEvent(new ChangeEvent(this, model));
    }

    private void updateLabels() {
        int label = 1;
        for (int i = 0; i < getComponentCount(); ++i) {
            if (getComponent(i) instanceof CollapsibleSection section) {
                section.setTitle(Integer.toString(label++));
            }
        }
    }
}
