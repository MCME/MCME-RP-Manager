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

package com.mcmiddleearth.rpmanager.gui.components.tree.actions;

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.tree.ResourcePackTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TreePasteAction extends Action {
    private final JTree tree;

    public TreePasteAction(JTree tree) {
        super("Paste", null, "Paste files from clipboard", KeyEvent.VK_P, KeyEvent.VK_V);
        this.tree = tree;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        StaticTreeNode node = (StaticTreeNode) tree.getLastSelectedPathComponent();
        if (node != null && !node.isDirectory()) {
            node = (StaticTreeNode) node.getParent();
        }
        if (node != null) {
            Transferable transferable =
                    Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                try {
                    java.util.List<File> files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (files != null) {
                        for (File file : files) {
                            node.addChild(ResourcePackTreeFactory.createNode(node, file));
                            if (file.isDirectory()) {
                                FileUtils.copyDirectoryToDirectory(file, node.getFile());
                            } else {
                                FileUtils.copyFileToDirectory(file, node.getFile());
                            }
                        }
                        ((DefaultTreeModel) tree.getModel()).reload(node);
                        tree.revalidate();
                        tree.repaint();
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    //nop
                }
            }
        }
    }
}
