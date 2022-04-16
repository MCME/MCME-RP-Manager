package com.mcmiddleearth.rpmanager.model.project;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Project implements Serializable {
    private final List<File> layers = new LinkedList<>();
    private String name;
    private File location;

    public Project() {}

    public Project(File minecraftJar, String name, File location) {
        layers.add(minecraftJar);
        this.name = name;
        this.location = location;
    }

    public List<File> getLayers() {
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
}
