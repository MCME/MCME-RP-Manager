/*
 * Copyright (C) 2021 MCME
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

package com.mcmiddleearth.rpmanager.model.wrappers;

import com.mcmiddleearth.rpmanager.model.BaseModel;

import java.util.List;

public abstract class ModelData<T extends BaseModel> {
    private T model;
    private List<TextureWrapper> textures;
    private ModelWrapper<?> parent;

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    public List<TextureWrapper> getTextures() {
        return textures;
    }

    public void setTextures(List<TextureWrapper> textures) {
        this.textures = textures;
    }

    public ModelWrapper<?> getParent() {
        return parent;
    }

    public void setParent(ModelWrapper<?> parent) {
        this.parent = parent;
    }
}
