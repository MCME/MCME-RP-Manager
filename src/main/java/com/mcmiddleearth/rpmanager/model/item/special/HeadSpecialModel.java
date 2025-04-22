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

package com.mcmiddleearth.rpmanager.model.item.special;

import com.google.gson.annotations.SerializedName;

public class HeadSpecialModel extends SpecialModel {
    private Kind kind;
    private String texture;
    private Float animation;

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Float getAnimation() {
        return animation;
    }

    public void setAnimation(Float animation) {
        this.animation = animation;
    }

    public enum Kind {
        @SerializedName("skeleton")
        SKELETON,
        @SerializedName("wither_skeleton")
        WITHER_SKELETON,
        @SerializedName("player")
        PLAYER,
        @SerializedName("zombie")
        ZOMBIE,
        @SerializedName("creeper")
        CREEPER,
        @SerializedName("piglin")
        PIGLIN,
        @SerializedName("dragon")
        DRAGON
    }
}
