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

import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.Form;
import com.mcmiddleearth.rpmanager.gui.components.TextInput;
import com.mcmiddleearth.rpmanager.model.internal.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SettingsModal extends JDialog {
    private final MainWindow window;
    private final Settings settings;

    public SettingsModal(MainWindow window, Settings settings) {
        super(window, "Settings", true);
        this.window = window;
        this.settings = settings;

        setLayout(new BorderLayout());
        SettingsForm settingsForm = new SettingsForm();
        add(settingsForm, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton save = new JButton(new Action("Save", "Save settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (FileWriter writer = new FileWriter(Settings.FILE)) {
                    writer.write(new GsonBuilder().create().toJson(settings));
                } catch (IOException ex) {
                    //TODO error dialog
                    throw new RuntimeException(ex);
                }
            }
        });
        buttonsPanel.add(save);
        JButton close = new JButton(new Action("Close", "Close settings") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SettingsModal.this.close();
            }
        });
        buttonsPanel.add(close);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private class SettingsForm extends Form {
        private final JComboBox<String> lookAndFeelCombo;
        private final TextInput imageEditorInput;

        public SettingsForm() {
            this.lookAndFeelCombo = new JComboBox<>(Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .map(UIManager.LookAndFeelInfo::getName).toArray(String[]::new));
            this.lookAndFeelCombo.setSelectedItem(settings.getLookAndFeel());
            this.lookAndFeelCombo.addItemListener(event -> {
                settings.setLookAndFeel((String) event.getItem());
                window.updateSettings();
            });

            this.imageEditorInput = new TextInput(settings.getImageEditor() == null ? "" : settings.getImageEditor(),
                    settings::setImageEditor, input -> {});

            addLabel(0, "GUI style: ");
            addInput(0, this.lookAndFeelCombo);
            addLabel(1, "Image editor: ");
            addInput(1, this.imageEditorInput);
        }
    }
}
