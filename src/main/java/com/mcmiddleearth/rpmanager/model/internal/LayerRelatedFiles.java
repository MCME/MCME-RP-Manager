/*
 * Copyright (C) 2025 MCME
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

package com.mcmiddleearth.rpmanager.model.internal;

import java.util.List;

public class LayerRelatedFiles {
    private final String layerName;
    private final List<SelectedFileData> relatedFiles;

    public LayerRelatedFiles(String layerName, List<SelectedFileData> relatedFiles) {
        this.layerName = layerName;
        this.relatedFiles = relatedFiles;
    }

    public String getLayerName() {
        return layerName;
    }

    public List<SelectedFileData> getRelatedFiles() {
        return relatedFiles;
    }
}
