package com.mcmiddleearth.rpmanager.model.project;

import java.io.File;

public class Layer {
    private final String name;
    private final File file;

    public Layer(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }
}
