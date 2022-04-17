package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.events.ListItemAddedEvent;
import com.mcmiddleearth.rpmanager.events.ListItemRemovedEvent;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.model.project.Project;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProjectFilesPane extends JPanel {
    private final JFileChooser fileChooser = new JFileChooser();
    private final Project project;

    public ProjectFilesPane(Project project) throws IOException {
        this.project = project;

        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Resource pack metadata file", "mcmeta"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileHidingEnabled(false);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setAlignmentY(TOP_ALIGNMENT);

        for (Layer layer : project.getLayers()) {
            LayerFilesPane layerFilesPane = new LayerFilesPane(layer);
            layerFilesPane.setAlignmentY(TOP_ALIGNMENT);
            add(layerFilesPane);
        }
        JButton addLayerButton = new JButton(new Action("+", "Add resource pack layer") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (fileChooser.showOpenDialog(ProjectFilesPane.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    project.addLayer(file.getParentFile().getName(), file);
                }
            }
        });
        addLayerButton.setAlignmentY(TOP_ALIGNMENT);
        add(addLayerButton);

        project.addLayerAddedListener(this::onLayerAdded);
        project.addLayerRemovedListener(this::onLayerRemoved);
    }

    @SuppressWarnings("unchecked")
    private void onLayerAdded(ListItemAddedEvent event) {
        try {
            LayerFilesPane layerFilesPane = new LayerFilesPane((Layer) event.getItem());
            layerFilesPane.setAlignmentY(TOP_ALIGNMENT);
            add(layerFilesPane, event.getIndex());
            revalidate();
            repaint();
        } catch (IOException e) {
            //TODO error dialog
            ((List<Layer>) event.getSource()).remove(event.getIndex());
        }
    }

    private void onLayerRemoved(ListItemRemovedEvent event) {
        remove(event.getIndex());
        revalidate();
        repaint();
    }
}
