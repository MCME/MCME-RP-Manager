/*
 * Copyright (C) 2024 MCME
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

public class RelatedFiles {
    private final List<SelectedFileData> relatedModels;
    private final List<SelectedFileData> relatedTextures;

    public RelatedFiles(List<SelectedFileData> relatedModels, List<SelectedFileData> relatedTextures) {
        this.relatedModels = relatedModels;
        this.relatedTextures = relatedTextures;
    }

    public List<SelectedFileData> getRelatedModels() {
        return relatedModels;
    }

    public List<SelectedFileData> getRelatedTextures() {
        return relatedTextures;
    }
}
