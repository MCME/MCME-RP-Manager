package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.gui.components.tree.JarTreeFactory;
import com.mcmiddleearth.rpmanager.model.project.Project;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ProjectPane extends JPanel {
    private final Project project;

    public ProjectPane(Project project) throws IOException {
        this.project = project;

        setLayout(new BorderLayout());
        add(new JLabel(project.getName()), BorderLayout.PAGE_START);
        add(new JTree(JarTreeFactory.createRootNode(project.getLayers().get(0))));
    }
}
