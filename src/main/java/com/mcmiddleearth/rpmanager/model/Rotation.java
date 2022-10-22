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

public class Rotation {
    private float[] origin;
    private Axis axis;
    private Float angle;
    private Boolean rescale;

    public float[] getOrigin() {
        return origin;
    }

    public void setOrigin(float[] origin) {
        this.origin = origin;
    }

    public Axis getAxis() {
        return axis;
    }

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public Float getAngle() {
        return angle;
    }

    public void setAngle(Float angle) {
        this.angle = angle;
    }

    public Boolean getRescale() {
        return rescale;
    }

    public void setRescale(Boolean rescale) {
        this.rescale = rescale;
    }
}
