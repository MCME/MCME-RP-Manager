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

package com.mcmiddleearth.rpmanager.gui.components;

import javax.swing.*;
import java.awt.*;

public class Form extends JPanel {
    public Form() {
        setLayout(new GridBagLayout());
    }

    protected void addLabel(int y, String label) {
        addLabel(y, new JLabel(label));
    }

    protected void addLabel(int y, Component component) {
        add(component, label(y));
    }

    protected void addInput(int y, Component component) {
        add(component, input(y));
    }

    private static GridBagConstraints label(int y) {
        return new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0);
    }

    private static GridBagConstraints input(int y) {
        return new GridBagConstraints(1, y, 1, 1, 1.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0);
    }
}
