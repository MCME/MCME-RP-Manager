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
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.Model;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BlockstateFileEditPane extends JPanel {
    private final BlockState blockState;

    public BlockstateFileEditPane(BlockState blockState) {
        this.blockState = blockState;

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.0;
        c.weightx = 1.0;

        if (!blockState.getVariants().isEmpty()) {
            boolean collapsed = blockState.getVariants().size() != 1;
            for (Map.Entry<String, List<Model>> variant : blockState.getVariants().entrySet()) {
                VariantEditPane variantEditPane = new VariantEditPane(variant.getKey(), variant.getValue());
                this.add(new CollapsibleSection(variant.getKey(), variantEditPane, collapsed), c);
                c.gridy++;
                this.add(new JSeparator(), c);
                c.gridy++;
            }
        } else {
            //TODO
        }

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1.0;
        this.add(Box.createVerticalGlue(), c);
    }
}
