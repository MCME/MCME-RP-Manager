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

package com.mcmiddleearth.rpmanager.model.item.range;

import com.google.gson.annotations.SerializedName;

public class CompassRangeProperty extends RangeProperty {
    private Target target;
    private Boolean wobble;

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Boolean getWobble() {
        return wobble;
    }

    public void setWobble(Boolean wobble) {
        this.wobble = wobble;
    }

    public enum Target {
        @SerializedName("spawn")
        SPAWN,
        @SerializedName("lodestone")
        LODESTONE,
        @SerializedName("recovery")
        RECOVERY,
        @SerializedName("none")
        NONE
    }
}
