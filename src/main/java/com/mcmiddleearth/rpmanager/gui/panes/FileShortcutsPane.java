/*
 * Copyright (C) 2024 MCME
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

import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.events.ListSelectionChangeEvent;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.components.VerticalLabelUI;
import com.mcmiddleearth.rpmanager.model.project.Project;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class FileShortcutsPane extends JPanel {
    private final Project project;
    private final JLabel recentFilesLabel;
    private final JLabel favoriteFilesLabel;
    private Component component = null;
    private ShownType shownType = ShownType.NONE;
    private final Border noBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    private final Border activeBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(), noBorder);
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public FileShortcutsPane(Project project) {
        this.project = project;

        setLayout(new BorderLayout());

        VerticalBox verticalBox = new VerticalBox();
        verticalBox.add(this.recentFilesLabel = new JLabel(" Recent files "));
        recentFilesLabel.setUI(new VerticalLabelUI(false));
        recentFilesLabel.addMouseListener(new MouseClickAdapter());
        recentFilesLabel.setFont(recentFilesLabel.getFont().deriveFont(Font.BOLD, 15.f));
        verticalBox.add(new JSeparator(JSeparator.HORIZONTAL));
        verticalBox.add(this.favoriteFilesLabel = new JLabel(" Favorite files "));
        favoriteFilesLabel.setUI(new VerticalLabelUI(false));
        favoriteFilesLabel.addMouseListener(new MouseClickAdapter());
        favoriteFilesLabel.setFont(favoriteFilesLabel.getFont().deriveFont(Font.BOLD, 15.f));

        add(verticalBox, BorderLayout.LINE_START);

        setBorder(BorderFactory.createEtchedBorder());
        updateLabels();
    }

    private void showRecentFiles() {
        showFiles(project.getRecentFilesFile(), ShownType.RECENT);
    }

    private void showFavoriteFiles() {
        showFiles(project.getFavoriteFilesFile(), ShownType.FAVORITES);
    }

    private void showFiles(File sourceFile, ShownType shownType) {
        try {
            if (this.shownType == shownType) {
                remove(this.component);
                this.component = null;
                this.shownType = ShownType.NONE;
            } else {
                List<String> files = getFiles(sourceFile);
                JList<String> list = new JList<>(files.toArray(String[]::new));
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                list.addListSelectionListener(this::onListSelectionChanged);
                if (this.shownType != ShownType.NONE) {
                    remove(this.component);
                }
                this.component = new FastScrollPane(list);
                add(this.component, BorderLayout.CENTER);
                this.shownType = shownType;
            }
            updateLabels();
            invalidate();
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Unknown error reading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> getFiles(File sourceFile) throws IOException {
        return (sourceFile.exists() ? Files.readAllLines(sourceFile.toPath()) : Collections.<String>emptyList())
                .stream().filter(s -> !s.isEmpty()).toList();
    }

    public void addListSelectionChangeListener(EventListener<ListSelectionChangeEvent> eventListener) {
        eventDispatcher.addEventListener(eventListener, ListSelectionChangeEvent.class);
    }

    private void updateLabels() {
        recentFilesLabel.setBorder(shownType == ShownType.RECENT ? activeBorder : noBorder);
        favoriteFilesLabel.setBorder(shownType == ShownType.FAVORITES ? activeBorder : noBorder);
    }

    private void onListSelectionChanged(ListSelectionEvent event) {
        eventDispatcher.dispatchEvent(
                new ListSelectionChangeEvent(event.getSource(), ((JList<?>) event.getSource()).getSelectedValue()));
    }

    private class MouseClickAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == recentFilesLabel && e.getButton() == MouseEvent.BUTTON1) {
                showRecentFiles();
            } else if (e.getSource() == favoriteFilesLabel && e.getButton() == MouseEvent.BUTTON1) {
                showFavoriteFiles();
            }
        }
    }

    private enum ShownType {
        NONE,
        RECENT,
        FAVORITES
    }
}
