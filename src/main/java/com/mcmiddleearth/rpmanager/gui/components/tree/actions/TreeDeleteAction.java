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

package com.mcmiddleearth.rpmanager.gui.components.tree.actions;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mcmiddleearth.rpmanager.utils.FileUtils.getFileRestoreData;
import static com.mcmiddleearth.rpmanager.utils.FileUtils.restoreFileData;

public class TreeDeleteAction extends Action {
    private final JTree tree;

    public TreeDeleteAction(JTree tree) {
        super("Delete", null, "Delete selected files", KeyEvent.VK_D, KeyEvent.VK_DELETE);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (tree.getSelectionPaths() != null && tree.getSelectionPaths().length > 0) {
            if (JOptionPane.showConfirmDialog(MainWindow.getInstance(),
                    "Do you want to delete all selected files?", "Delete confirmation",
                    JOptionPane.YES_NO_OPTION) == 0) {
                com.mcmiddleearth.rpmanager.utils.Action undoAction = () -> {};
                com.mcmiddleearth.rpmanager.utils.Action redoAction = () -> {};
                for (TreePath path : tree.getSelectionPaths()) {
                    StaticTreeNode node = (StaticTreeNode) path.getLastPathComponent();
                    StaticTreeNode parentNode = (StaticTreeNode) node.getParent();
                    parentNode.getChildren().remove(node);
                    File targetFile = node.getFile();
                    Map<String, Object> restoreData = new LinkedHashMap<>();
                    restoreData.put(node.getFile().getName(), getFileRestoreData(targetFile));
                    undoAction = undoAction.butFirst(() -> restoreFileData(parentNode.getFile(), restoreData));
                    redoAction = redoAction.then(() -> {
                        if (targetFile.isDirectory()) {
                            FileUtils.deleteDirectory(targetFile);
                        } else {
                            targetFile.delete();
                        }
                    });
                }
                MainWindow.getInstance().getActionManager().submit(undoAction, redoAction);
                ((DefaultTreeModel) tree.getModel()).reload();
            }
        }
    }
}
