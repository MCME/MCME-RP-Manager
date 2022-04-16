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
