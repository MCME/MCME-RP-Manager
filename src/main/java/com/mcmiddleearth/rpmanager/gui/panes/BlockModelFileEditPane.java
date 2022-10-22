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

import com.mcmiddleearth.rpmanager.model.BlockModel;

import javax.swing.*;

public class BlockModelFileEditPane extends BaseModelFileEditPane {
    private final String fileName;

    public BlockModelFileEditPane(String fileName, BlockModel blockModel) {
        super(blockModel);
        this.fileName = fileName;

        this.add(parentPanel);

        JCheckBox ambientOcclusionInput = new JCheckBox(
                "Ambient occlusion", Boolean.TRUE.equals(blockModel.getAmbientocclusion()));
        ambientOcclusionInput.addItemListener(event -> {
            blockModel.setAmbientocclusion(ambientOcclusionInput.isSelected() ? true : null);
            onChange();
        });
        this.add(ambientOcclusionInput);

        this.add(displayPanel);
        this.add(texturesPanel);
        this.add(elementsPanel);
    }
}
