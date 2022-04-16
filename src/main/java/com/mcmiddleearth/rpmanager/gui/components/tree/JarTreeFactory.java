package com.mcmiddleearth.rpmanager.gui.components.tree;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.zip.ZipFile;

public class JarTreeFactory {
    private JarTreeFactory() {}

    public static StaticTreeNode createRootNode(File jarFile) throws IOException {
        String filter = "assets/minecraft/";
        StaticTreeNode staticTreeNode = new StaticTreeNode(null, "assets", new LinkedList<>());
        staticTreeNode.getChildren().add(new StaticTreeNode(staticTreeNode, "minecraft", new LinkedList<>()));
        new ZipFile(jarFile).stream()
                .filter(entry -> entry.getName().startsWith(filter))
                .filter(entry -> !entry.isDirectory())
                .forEach(entry -> addEntry(staticTreeNode, entry.getName()));
        sortNodes(staticTreeNode);
        return staticTreeNode;
    }

    private static void addEntry(StaticTreeNode node, String entryName) {
        addEntry(node, entryName.split("/"), 1);
    }

    private static void addEntry(StaticTreeNode node, String[] nameParts, int offset) {
        if (offset == nameParts.length - 1) {
            node.getChildren().add(new StaticTreeNode(node, nameParts[offset], new LinkedList<>()));
        } else {
            StaticTreeNode childNode = node.getChildren().stream()
                    .filter(n -> n.getName().equals(nameParts[offset]))
                    .findFirst()
                    .orElseGet(() -> {
                        StaticTreeNode child = new StaticTreeNode(node, nameParts[offset], new LinkedList<>());
                        node.getChildren().add(child);
                        return child;
                    });
            addEntry(childNode, nameParts, offset + 1);
        }
    }

    private static void sortNodes(StaticTreeNode node) {
        for (StaticTreeNode child : node.getChildren()) {
            sortNodes(child);
        }
        node.getChildren().sort(Comparator.comparing(StaticTreeNode::getName));
    }
}
