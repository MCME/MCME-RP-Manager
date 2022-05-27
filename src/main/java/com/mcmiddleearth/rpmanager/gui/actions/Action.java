package com.mcmiddleearth.rpmanager.gui.actions;

import javax.swing.*;
import java.awt.event.KeyEvent;

public abstract class Action extends AbstractAction {
    private final String name;
    private final String description;

    protected Action(String name, String description) {
        super(name);
        putValue(SHORT_DESCRIPTION, description);

        this.name = name;
        this.description = description;
    }

    protected Action(String name, Icon icon, String description, Integer mnemonic, Integer acceleratorKey) {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, description);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(acceleratorKey, KeyEvent.CTRL_DOWN_MASK));

        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
