package com.mcmiddleearth.rpmanager.model.project;

import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.events.ListItemAddedEvent;
import com.mcmiddleearth.rpmanager.events.ListItemRemovedEvent;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Project implements Serializable {
    private final List<Layer> layers = new LinkedList<>();
    private String name;
    private File location;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public Project() {}

    public Project(File minecraftJar, String name, File location) {
        layers.add(new Layer("Vanilla", minecraftJar));
        this.name = name;
        this.location = location;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getLocation() {
        return location;
    }

    public void setLocation(File location) {
        this.location = location;
    }

    public void addLayer(String name, File location) {
        addLayer(new Layer(name, location));
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
        eventDispatcher.dispatchEvent(new ListItemAddedEvent(layers, layer, layers.indexOf(layer)));
    }

    public void removeLayer(Layer layer) {
        int index = layers.indexOf(layer);
        layers.remove(index);
        eventDispatcher.dispatchEvent(new ListItemRemovedEvent(layers, layer, index));
    }

    public void addLayerAddedListener(EventListener<ListItemAddedEvent> listener) {
        eventDispatcher.addEventListener(listener, ListItemAddedEvent.class);
    }

    public void addLayerRemovedListener(EventListener<ListItemRemovedEvent> listener) {
        eventDispatcher.addEventListener(listener, ListItemRemovedEvent.class);
    }
}
