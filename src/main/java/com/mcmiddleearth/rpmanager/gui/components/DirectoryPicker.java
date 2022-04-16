package com.mcmiddleearth.rpmanager.gui.components;

import javax.swing.*;

public class DirectoryPicker extends FilePicker {
    @Override
    protected void configureFileChooser(JFileChooser fileChooser) {
        super.configureFileChooser(fileChooser);

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
}
