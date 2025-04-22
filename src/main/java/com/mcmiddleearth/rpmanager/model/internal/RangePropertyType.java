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

import com.mcmiddleearth.rpmanager.model.item.range.*;

import java.util.stream.Stream;

public enum RangePropertyType {
    BUNDLE_FULLNESS("minecraft:bundle/fullness", BundleFullnessRangeProperty.class),
    COMPASS("minecraft:compass", CompassRangeProperty.class, "target", "wobble"),
    COOLDOWN("minecraft:cooldown", CooldownRangeProperty.class),
    COUNT("minecraft:count", CountRangeProperty.class, "normalize"),
    CROSSBOW_PULL("minecraft:crossbow/pull", CrossbowPullRangeProperty.class),
    DAMAGE("minecraft:damage", DamageRangeProperty.class, "normalize"),
    TIME("minecraft:time", TimeRangeProperty.class, "source", "wobble"),
    USE_CYCLE("minecraft:use_cycle", UseCycleRangeProperty.class, "period"),
    USE_DURATION("minecraft:use_duration", UseDurationRangeProperty.class, "remaining"),
    CUSTOM_MODEL_DATA("minecraft:custom_model_data", CustomModelDataRangeProperty.class, "index"),
    ;

    private final String id;
    private final Class<? extends RangeProperty> typeClass;
    private final String[] additionalFields;

    RangePropertyType(String id, Class<? extends RangeProperty> typeClass, String... additionalFields) {
        this.id = id;
        this.typeClass = typeClass;
        this.additionalFields = additionalFields;
    }

    public String getId() {
        return id;
    }

    public Class<? extends RangeProperty> getTypeClass() {
        return typeClass;
    }

    public String[] getAdditionalFields() {
        return additionalFields;
    }

    public static RangePropertyType byId(String id) {
        return Stream.of(values()).filter(r -> r.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid range property type: " + id));
    }

    public static RangePropertyType byRangeProperty(RangeProperty rangeProperty) {
        return Stream.of(values()).filter(r -> r.getTypeClass().isAssignableFrom(rangeProperty.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid range property type: " + rangeProperty.getClass().getCanonicalName()));
    }
}
