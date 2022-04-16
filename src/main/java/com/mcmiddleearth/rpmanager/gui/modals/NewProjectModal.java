package com.mcmiddleearth.rpmanager.gui.modals;

import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.DirectoryPicker;
import com.mcmiddleearth.rpmanager.gui.components.FilePicker;
import com.mcmiddleearth.rpmanager.gui.utils.FormButtonEnabledListener;
import com.mcmiddleearth.rpmanager.model.project.Project;
import com.mcmiddleearth.rpmanager.model.project.Session;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class NewProjectModal extends JDialog {
    public NewProjectModal(Frame parent, Session session) {
        super(parent, "Create new project", true);
        setLayout(new BorderLayout());
        NewProjectForm newProjectForm = new NewProjectForm();
        add(newProjectForm, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton create = new JButton(new Action("Create", "Create project") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                session.addProject(new Project(newProjectForm.getMinecraftLocation(), newProjectForm.getProjectName(),
                        newProjectForm.getProjectDir()));
                NewProjectModal.this.close();
            }
        });
        new FormButtonEnabledListener(create.getModel(), newProjectForm.getDocuments());
        buttonsPanel.add(create);
        JButton cancel = new JButton(new Action("Cancel", "Cancel creating project") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NewProjectModal.this.close();
            }
        });
        buttonsPanel.add(cancel);
        add(buttonsPanel, BorderLayout.PAGE_END);

        pack();
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private static class NewProjectForm extends JPanel {
        private final FilePicker minecraftLocationPicker;
        private final JTextField projectNameInput;
        private final DirectoryPicker projectDirPicker;

        private NewProjectForm() {
            setLayout(new GridBagLayout());

            minecraftLocationPicker = new FilePicker(new FileNameExtensionFilter("JAR file", "jar"));
            projectNameInput = new JTextField();
            projectDirPicker = new DirectoryPicker();

            add(new JLabel("Minecraft .jar location"), label(0));
            add(minecraftLocationPicker, input(0));
            add(new JLabel("Project name"), label(1));
            add(projectNameInput, input(1));
            add(new JLabel("Project settings location"), label(2));
            add(projectDirPicker, input(2));
        }

        public List<Document> getDocuments() {
            return Arrays.asList(minecraftLocationPicker.getDocument(), projectNameInput.getDocument(),
                    projectDirPicker.getDocument());
        }

        public File getMinecraftLocation() {
            return new File(minecraftLocationPicker.getSelectedFilePath());
        }

        public String getProjectName() {
            return projectNameInput.getText();
        }

        public File getProjectDir() {
            return new File(projectDirPicker.getSelectedFilePath());
        }
    }

    private static GridBagConstraints label(int y) {
        return new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0);
    }

    private static GridBagConstraints input(int y) {
        return new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0);
    }
}
