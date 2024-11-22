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

package com.mcmiddleearth.rpmanager.gui.components.tree.actions;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddToFavoritesAction extends Action {
    private final JTree tree;

    public AddToFavoritesAction(JTree tree) {
        super("Add to favorites", null, "Add selected files to favorites",
                KeyEvent.VK_A, KeyEvent.VK_G);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> files = new LinkedList<>();
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : tree.getSelectionPaths()) {
                StaticTreeNode node = (StaticTreeNode) path.getLastPathComponent();
                if (!node.isDirectory()) {
                    files.add(Stream.of(path.getPath()).map(Object::toString).collect(Collectors.joining("/")));
                }
            }
        }
        addToFavorites(files);
    }

    private void addToFavorites(List<String> files) {
        File favoritesFile = MainWindow.getInstance().getCurrentProject().getFavoriteFilesFile();
        try {
            List<String> currentFavorites = favoritesFile.exists() ?
                    Files.readAllLines(favoritesFile.toPath()) : Collections.emptyList();
            List<String> newFavorites = new ArrayList<>(currentFavorites);
            files.stream().filter(f -> !currentFavorites.contains(f)).forEach(newFavorites::add);
            Files.write(favoritesFile.toPath(), newFavorites);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Unknown error updating favorites: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
