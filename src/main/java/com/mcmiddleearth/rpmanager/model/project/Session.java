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

    public List<Project> getProjects() {
        return projects;
    }

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
