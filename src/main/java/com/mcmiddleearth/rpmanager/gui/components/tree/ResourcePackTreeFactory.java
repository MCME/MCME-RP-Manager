package com.mcmiddleearth.rpmanager.gui.components.tree;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class ResourcePackTreeFactory {
    private ResourcePackTreeFactory() {}

    public static StaticTreeNode createRootNode(File resourcePackMeta) {
        return createNode(null, resourcePackMeta.getParentFile());
    }

    private static StaticTreeNode createNode(StaticTreeNode parent, File file) {
        StaticTreeNode node = new StaticTreeNode(parent, file.getName(), new LinkedList<>());
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(f -> node.getChildren().add(createNode(node, f)));
        }
        return node;
    }
}
