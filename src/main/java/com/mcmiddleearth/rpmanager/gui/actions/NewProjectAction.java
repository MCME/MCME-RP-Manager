package com.mcmiddleearth.rpmanager.gui.actions;

import com.mcmiddleearth.rpmanager.gui.Icons;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.modals.NewProjectModal;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class NewProjectAction extends Action {
    protected NewProjectAction() {
        super("New project...", Icons.NEW_PROJECT, "Create new project", KeyEvent.VK_N,
                KeyEvent.VK_N);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MainWindow mainWindow = MainWindow.getInstance();
        new NewProjectModal(mainWindow, mainWindow.getSession());
    }
}
