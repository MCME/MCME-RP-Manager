package com.mcmiddleearth.rpmanager.gui;

import com.mcmiddleearth.rpmanager.gui.actions.Actions;
import com.mcmiddleearth.rpmanager.gui.panes.ProjectsPane;
import com.mcmiddleearth.rpmanager.model.project.Session;
import com.mcmiddleearth.rpmanager.model.wrappers.ResourcePackData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeSupport;

public class MainWindow extends JFrame {
    private static MainWindow INSTANCE;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private ResourcePackData resourcePackData = null;
    private final Session session = new Session();

    public MainWindow() {
        INSTANCE = this;
        createMenu();
        setTitle("MCME Resource Pack Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(new ProjectsPane(session), BorderLayout.CENTER);
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(Actions.NEW_PROJECT);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    public ResourcePackData getResourcePackData() {
        return resourcePackData;
    }

    public void setResourcePackData(ResourcePackData resourcePackData) {
        ResourcePackData oldValue = this.resourcePackData;
        this.resourcePackData = resourcePackData;
        propertyChangeSupport.firePropertyChange("resourcePackData", oldValue, resourcePackData);
    }

    public Session getSession() {
        return session;
    }

    public static MainWindow getInstance() {
        return INSTANCE;
    }
}
