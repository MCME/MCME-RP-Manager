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

package com.mcmiddleearth.rpmanager.gui.components.tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarTreeFactory {
    private JarTreeFactory() {}

    public static StaticTreeNode createRootNode(File jarFile) throws IOException {
        String filter = "assets/minecraft/";
        File f = Files.createTempDirectory("mcme-rp-manager-vanilla-rp").toFile();
        f.deleteOnExit();
        File assets = new File(f, "assets");
        assets.mkdir();
        File minecraft = new File(assets, "minecraft");
        minecraft.mkdir();
        StaticTreeNode staticTreeNode = new StaticTreeNode(null, "assets", assets, true, new LinkedList<>());
        staticTreeNode.getChildren().add(new StaticTreeNode(staticTreeNode, "minecraft", minecraft, true, new LinkedList<>()));
        ZipFile zipFile = new ZipFile(jarFile);
        zipFile.stream()
                .filter(entry -> entry.getName().startsWith(filter))
                .forEach(entry -> addEntry(staticTreeNode, zipFile, entry, minecraft, entry.isDirectory()));
        sortNodes(staticTreeNode);
        return staticTreeNode;
    }

    private static void addEntry(StaticTreeNode node, ZipFile zipFile, ZipEntry entry, File parentFile,
                                 boolean isDirectory) {
        if (isDirectory) {
            addEntry(node, entry.getName().split("/"), parentFile, null, isDirectory, 1);
        } else {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                addEntry(node, entry.getName().split("/"), parentFile, inputStream, isDirectory, 1);
            } catch (IOException e) {
                //TODO error dialog
                e.printStackTrace();
            }
        }
    }

    private static void addEntry(StaticTreeNode node, String[] nameParts, File parentFile, InputStream inputStream,
                                 boolean isDirectory, int offset) {
        String name = nameParts[offset];
        if (offset == nameParts.length - 1) {
            File f = isDirectory ? createDirectory(parentFile, name) : createFile(parentFile, name, inputStream);
            node.getChildren().add(new StaticTreeNode(node, name, f, isDirectory, new LinkedList<>()));
        } else {
            StaticTreeNode childNode = node.getChildren().stream()
                    .filter(n -> n.getName().equals(name))
                    .findFirst()
                    .orElseGet(() -> {
                        StaticTreeNode child = new StaticTreeNode(
                                node, name, createDirectory(parentFile, name), true, new LinkedList<>());
                        node.getChildren().add(child);
                        return child;
                    });
            addEntry(childNode, nameParts, childNode.getFile(), inputStream, isDirectory, offset + 1);
        }
    }

    private static File createDirectory(File parent, String name) {
        File f = new File(parent, name);
        f.mkdir();
        return f;
    }

    private static File createFile(File parent, String name, InputStream inputStream) {
        File f = new File(parent, name);
        try (FileOutputStream outputStream = new FileOutputStream(f)) {
            outputStream.write(inputStream.readAllBytes());
        } catch (IOException e) {
            //TODO error dialog
            e.printStackTrace();
        }
        return f;
    }

    private static void sortNodes(StaticTreeNode node) {
        for (StaticTreeNode child : node.getChildren()) {
            sortNodes(child);
        }
        node.getChildren().sort(Comparator.comparing(StaticTreeNode::getName));
    }
}
