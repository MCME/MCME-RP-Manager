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

package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.gui.actions.Actions;
import com.mcmiddleearth.rpmanager.gui.panes.ProjectsPane;
import com.mcmiddleearth.rpmanager.model.internal.Settings;
import com.mcmiddleearth.rpmanager.model.project.Project;
import com.mcmiddleearth.rpmanager.model.project.Session;
import com.mcmiddleearth.rpmanager.utils.ActionManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MainWindow extends JFrame {
    private static MainWindow INSTANCE;
    private final Session session = new Session();
    private final ProjectsPane projectsPane;
    private final Settings settings;

    public MainWindow(Settings settings) {
        this.settings = settings;
        INSTANCE = this;
        createMenu();
        setTitle("MCME Resource Pack Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(this.projectsPane = new ProjectsPane(session), BorderLayout.CENTER);
        setVisible(true);
        updateSettings();
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(Actions.NEW_PROJECT);
        fileMenu.add(Actions.OPEN_PROJECT);
        fileMenu.add(Actions.SAVE_PROJECT);
        fileMenu.addSeparator();
        fileMenu.add(Actions.SETTINGS);
        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        editMenu.add(Actions.UNDO);
        editMenu.add(Actions.REDO);
        menuBar.add(editMenu);
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        toolsMenu.add(Actions.COMPILE);
        menuBar.add(toolsMenu);

        setJMenuBar(menuBar);
    }

    public Session getSession() {
        return session;
    }

    public Project getCurrentProject() {
        return projectsPane.getCurrentProject();
    }

    public Settings getSettings() {
        return settings;
    }

    public ActionManager getActionManager() {
        return projectsPane.getActionManager();
    }

    public void updateSettings() {
        try {
            UIManager.setLookAndFeel(Arrays.stream(UIManager.getInstalledLookAndFeels())
                    .filter(lf -> lf.getName().equals(settings.getLookAndFeel()))
                    .findFirst().orElseThrow().getClassName());
            SwingUtilities.updateComponentTreeUI(this);
            pack();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            //TODO show error dialog
            throw new RuntimeException(e);
        }
    }

    public static MainWindow getInstance() {
        return INSTANCE;
    }
}
