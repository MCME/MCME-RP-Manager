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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.model.Model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class VariantEditPane extends JPanel {
    private final String variant;
    private final List<Model> models;

    public VariantEditPane(String variant, List<Model> models) {
        this.variant = variant;
        this.models = models;

        setBorder(new EmptyBorder(0, 10, 0, 0));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.0;
        c.weightx = 1.0;

        boolean collapsed = models.size() != 1;
        int label = 1;
        for (Model model : models) {
            VariantModelEditPane variantModelEditPane = new VariantModelEditPane(model);
            this.add(new CollapsibleSection(Integer.toString(label++), variantModelEditPane, collapsed), c);
            c.gridy++;
            this.add(new JSeparator(), c);
            c.gridy++;
        }

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1.0;
        this.add(Box.createVerticalGlue(), c);
    }
}
