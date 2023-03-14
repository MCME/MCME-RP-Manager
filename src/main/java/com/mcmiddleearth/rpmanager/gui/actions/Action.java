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

    protected Action(String name, Icon icon, String description) {
        super(name, icon);
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
