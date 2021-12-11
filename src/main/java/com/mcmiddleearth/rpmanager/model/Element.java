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

import java.util.Map;

public class Element {
    private float[] from;
    private float[] to;
    private Rotation rotation;
    private Boolean shade;
    private Map<Face, FaceDefinition> faces;

    public float[] getFrom() {
        return from;
    }

    public void setFrom(float[] from) {
        this.from = from;
    }

    public float[] getTo() {
        return to;
    }

    public void setTo(float[] to) {
        this.to = to;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Boolean getShade() {
        return shade;
    }

    public void setShade(Boolean shade) {
        this.shade = shade;
    }

    public Map<Face, FaceDefinition> getFaces() {
        return faces;
    }

    public void setFaces(Map<Face, FaceDefinition> faces) {
        this.faces = faces;
    }
}
