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

package com.mcmiddleearth.rpmanager.model;

public class FaceDefinition {
    private float[] uv;
    private String texture;
    private Face cullface;
    private Integer rotation;
    private Integer tintindex;

    public float[] getUv() {
        return uv;
    }

    public void setUv(float[] uv) {
        this.uv = uv;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Face getCullface() {
        return cullface;
    }

    public void setCullface(Face cullface) {
        this.cullface = cullface;
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public Integer getTintindex() {
        return tintindex;
    }

    public void setTintindex(Integer tintindex) {
        this.tintindex = tintindex;
    }
}
