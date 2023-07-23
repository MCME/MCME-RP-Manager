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
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.Case;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CaseEditPane extends VerticalBox {
    private final Case theCase;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public CaseEditPane(String fileName, Case theCase) {
        this.theCase = theCase;

        setBorder(new EmptyBorder(0, 10, 0, 0));

        CaseWhenEditPane caseWhenEditPane = new CaseWhenEditPane(fileName, theCase);
        caseWhenEditPane.addChangeListener(this::onChange);
        this.add(new CollapsibleSection("When", caseWhenEditPane, false));
        this.add(new JSeparator());

        CaseApplyEditPane caseApplyEditPane = new CaseApplyEditPane(theCase);
        caseApplyEditPane.addChangeListener(this::onChange);
        this.add(new CollapsibleSection("Apply", caseApplyEditPane, false));
        this.add(new JSeparator());
    }

    public Case getCase() {
        return theCase;
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
