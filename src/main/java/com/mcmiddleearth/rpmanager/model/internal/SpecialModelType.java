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

import com.mcmiddleearth.rpmanager.model.item.special.*;

import java.util.Arrays;

public enum SpecialModelType {
    BANNER("minecraft:banner", BannerSpecialModel.class),
    BED("minecraft:bed", BedSpecialModel.class),
    CHEST("minecraft:chest", ChestSpecialModel.class),
    CONDUIT("minecraft:conduit", ConduitSpecialModel.class),
    DECORATED_POT("minecraft:decorated_pot", DecoratedPotSpecialModel.class),
    HEAD("minecraft:head", HeadSpecialModel.class),
    SHIELD("minecraft:shield", ShieldSpecialModel.class),
    SHULKER_BOX("minecraft:shulker_box", ShulkerBoxSpecialModel.class),
    STANDING_SIGN("minecraft:standing_sign", StandingSignSpecialModel.class),
    HANGING_SIGN("minecraft:hanging_sign", HangingSignSpecialModel.class),
    TRIDENT("minecraft:trident", TridentSpecialModel.class),
    ;
    private final String id;
    private final Class<? extends SpecialModel> typeClass;

    SpecialModelType(String id, Class<? extends SpecialModel> typeClass) {
        this.id = id;
        this.typeClass = typeClass;
    }

    public String getId() {
        return id;
    }

    public Class<? extends SpecialModel> getTypeClass() {
        return typeClass;
    }

    public static SpecialModelType byId(String id) {
        return Arrays.stream(values()).filter(s -> s.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid special model type: " + id));
    }

    public static SpecialModelType bySpecialModel(SpecialModel specialModel) {
        return Arrays.stream(values()).filter(s -> s.getTypeClass().isAssignableFrom(specialModel.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid special model type: " + specialModel.getClass().getCanonicalName()));
    }
}
