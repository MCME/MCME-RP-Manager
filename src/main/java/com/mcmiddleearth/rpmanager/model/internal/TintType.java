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

import com.mcmiddleearth.rpmanager.model.item.tint.*;

import java.util.Arrays;

public enum TintType {
    CONSTANT("minecraft:constant", ConstantTint.class),
    DYE("minecraft:dye", DyeTint.class),
    FIREWORK("minecraft:firework", FireworkTint.class),
    GRASS("minecraft:grass", GrassTint.class),
    MAP_COLOR("minecraft:map_color", MapColorTint.class),
    POTION("minecraft:potion", PotionTint.class),
    TEAM("minecraft:team", TeamTint.class),
    CUSTOM_MODEL_DATA("minecraft:custom_model_data", CustomModelDataTint.class),
    ;
    private final String id;
    private final Class<? extends Tint> typeClass;

    TintType(String id, Class<? extends Tint> typeClass) {
        this.id = id;
        this.typeClass = typeClass;
    }

    public String getId() {
        return id;
    }

    public Class<? extends Tint> getTypeClass() {
        return typeClass;
    }

    public static TintType byId(String id) {
        return Arrays.stream(values()).filter(t -> t.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tint type: " + id));
    }

    public static TintType byTint(Tint tint) {
        return Arrays.stream(values()).filter(t -> t.getTypeClass().isAssignableFrom(tint.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Invalid tint type: " + tint.getClass().getCanonicalName()));
    }
}
