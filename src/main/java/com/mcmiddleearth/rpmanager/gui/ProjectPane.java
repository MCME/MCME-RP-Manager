package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.model.project.Project;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ProjectPane extends JPanel {
    private final Project project;

    public ProjectPane(Project project) throws IOException {
        this.project = project;

        setLayout(new BorderLayout());

        ProjectFilesPane treesPane = new ProjectFilesPane(project);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                new JScrollPane(treesPane),
                new FileEditPane());
        innerSplitPane.setDividerSize(1);
        innerSplitPane.setOneTouchExpandable(false);
        innerSplitPane.setResizeWeight(0.5);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                innerSplitPane,
                new JPanel());
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.66);

        add(splitPane, BorderLayout.CENTER);
    }
}
