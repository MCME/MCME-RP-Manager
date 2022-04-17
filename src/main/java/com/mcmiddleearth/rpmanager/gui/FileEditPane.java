package com.mcmiddleearth.rpmanager.gui;

import javax.swing.*;
import java.awt.*;

public class FileEditPane extends JPanel {
    public FileEditPane() {
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                new JPanel(),
                new JPanel());
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }
}
