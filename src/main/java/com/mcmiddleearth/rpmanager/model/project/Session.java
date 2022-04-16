package com.mcmiddleearth.rpmanager.model.project;

import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.events.ListItemAddedEvent;
import com.mcmiddleearth.rpmanager.events.ListItemRemovedEvent;

import java.util.LinkedList;
import java.util.List;

public class Session {
    private final List<Project> projects = new LinkedList<>();
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public void addProject(Project project) {
        projects.add(project);
        eventDispatcher.dispatchEvent(new ListItemAddedEvent(projects, project, projects.indexOf(project)));
    }

    public void closeProject(Project project) {
        int index = projects.indexOf(project);
        projects.remove(project);
        eventDispatcher.dispatchEvent(new ListItemRemovedEvent(projects, project, index));
    }

    public void addProjectAddedListener(EventListener<ListItemAddedEvent> listener) {
        eventDispatcher.addEventListener(listener, ListItemAddedEvent.class);
    }

    public void addProjectRemovedListener(EventListener<ListItemRemovedEvent> listener) {
        eventDispatcher.addEventListener(listener, ListItemRemovedEvent.class);
    }
}
