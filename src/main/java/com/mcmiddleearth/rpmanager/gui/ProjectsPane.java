package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.events.ListItemAddedEvent;
import com.mcmiddleearth.rpmanager.events.ListItemRemovedEvent;
import com.mcmiddleearth.rpmanager.model.project.Project;
import com.mcmiddleearth.rpmanager.model.project.Session;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class ProjectsPane extends JTabbedPane {
    private final Session session;

    public ProjectsPane(Session session) {
        this.session = session;

        session.addProjectAddedListener(this::onProjectAdded);
        session.addProjectRemovedListener(this::onProjectClosed);
    }

    @SuppressWarnings("unchecked")
    private void onProjectAdded(ListItemAddedEvent event) {
        Project project = (Project) event.getItem();
        try {
            insertTab(project.getName(), null, new ProjectPane(project), project.getName(), event.getIndex());
        } catch (IOException e) {
            ((List<Project>) event.getSource()).remove(event.getIndex());
            //TODO error dialog
        }
    }

    private void onProjectClosed(ListItemRemovedEvent event) {
        remove(event.getIndex());
    }
}
