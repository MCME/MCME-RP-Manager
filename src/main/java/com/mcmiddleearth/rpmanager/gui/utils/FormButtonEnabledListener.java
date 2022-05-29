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

package com.mcmiddleearth.rpmanager.gui.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.util.List;

public class FormButtonEnabledListener implements DocumentListener {
    private final List<Document> requiredFields;
    private final ButtonModel buttonModel;

    public FormButtonEnabledListener(ButtonModel buttonModel, List<Document> requiredFields) {
        this.buttonModel = buttonModel;
        this.requiredFields = requiredFields;
        requiredFields.forEach(d -> d.addDocumentListener(this));
        updateButton();
    }

    private void updateButton() {
        buttonModel.setEnabled(requiredFields.stream().allMatch(d -> d.getLength() > 0));
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        updateButton();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        updateButton();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        updateButton();
    }
}
