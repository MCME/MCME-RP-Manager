package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.gui.components.tree.JarTreeFactory;
import com.mcmiddleearth.rpmanager.gui.components.tree.ResourcePackTreeFactory;
import com.mcmiddleearth.rpmanager.model.project.Layer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LayerFilesPane extends JPanel {
    private final Layer layer;

    public LayerFilesPane(Layer layer) throws IOException {
        this.layer = layer;

        setLayout(new BorderLayout());
        add(new JLabel(layer.getName()), BorderLayout.PAGE_START);
        add(createTree(layer.getFile()), BorderLayout.CENTER);
    }

    private static JTree createTree(File file) throws IOException {
        if (file.getName().endsWith(".jar")) {
            return new JTree(JarTreeFactory.createRootNode(file));
        } else {
            return new JTree(ResourcePackTreeFactory.createRootNode(file));
        }
    }
}
