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

import com.google.gson.*;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class FileEditModal extends JDialog {
    private static final Gson GSON = new GsonBuilder()
            .setLenient().setPrettyPrinting().enableComplexMapKeySerialization().create();

    public FileEditModal(Frame parent, String text, Class<?> type, Consumer<String> onAccept) {
        super(parent, "Edit file", true);

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(400, 400));

        JTextArea textArea = new JTextArea(0, 0);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setText(text);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(textArea);

        this.add(new FastScrollPane(contentPane), BorderLayout.CENTER);

        JButton accept = new JButton(new Action("Save", "Save file") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    //TODO silly workaround to detect errors because of a critical error in GSON
                    //     which makes it accept everything regardless of the desired object type
                    String newJson = GSON.toJson(GSON.fromJson(textArea.getText(), type));
                    JsonElement element1 = JsonParser.parseString(textArea.getText());
                    JsonElement element2 = JsonParser.parseString(newJson);
                    if (!element1.equals(element2)) {
                        throw new JsonSyntaxException("Invalid JSON object (no further details available due to a " +
                                "critical error in GSON library)");
                    }
                    onAccept.accept(textArea.getText());
                    FileEditModal.this.close();
                } catch (JsonSyntaxException e) {
                    JOptionPane.showMessageDialog(FileEditModal.this, e.getMessage(),
                            "JSON parse error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating variant") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileEditModal.this.close();
            }
        });
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonsPanel.add(accept);
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        setLocation(MainWindow.getInstance().getMousePosition());
        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
