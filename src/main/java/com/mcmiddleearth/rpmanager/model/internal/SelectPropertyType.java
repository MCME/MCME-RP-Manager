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

import com.mcmiddleearth.rpmanager.model.item.select.*;

import java.util.stream.Stream;

public enum SelectPropertyType {
    BLOCK_STATE("minecraft:block_state", BlockStateSelectProperty.class, "block_state_property"),
    CHARGE_TYPE("minecraft:charge_type", ChargeTypeSelectProperty.class),
    COMPONENT("minecraft:component", ComponentSelectProperty.class, "component"),
    CONTEXT_DIMENSION("minecraft:context_dimension", ContextDimensionSelectProperty.class),
    CONTEXT_ENTITY_TYPE("minecraft:context_entity_type", ContextEntityTypeSelectProperty.class),
    DISPLAY_CONTEXT("minecraft:display_context", DisplayContextSelectProperty.class),
    LOCAL_TIME("minecraft:local_time", LocalTimeSelectProperty.class, "locale", "time_zone", "pattern"),
    MAIN_HAND("minecraft:main_hand", MainHandSelectProperty.class),
    TRIM_MATERIAL("minecraft:trim_material", TrimMaterialSelectProperty.class),
    CUSTOM_MODEL_DATA("minecraft:custom_model_data", CustomModelDataSelectProperty.class, "index"),
    ;

    private final String id;
    private final Class<? extends SelectProperty> typeClass;
    private final String[] additionalFields;

    SelectPropertyType(String id, Class<? extends SelectProperty> typeClass, String... additionalFields) {
        this.id = id;
        this.typeClass = typeClass;
        this.additionalFields = additionalFields;
    }

    public String getId() {
        return id;
    }

    public Class<? extends SelectProperty> getTypeClass() {
        return typeClass;
    }

    public String[] getAdditionalFields() {
        return additionalFields;
    }

    public static SelectPropertyType byId(String id) {
        return Stream.of(values()).filter(s -> s.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid select property type: " + id));
    }

    public static SelectPropertyType bySelectProperty(SelectProperty selectProperty) {
        return Stream.of(values()).filter(s -> s.getTypeClass().isAssignableFrom(selectProperty.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid select property type: " + selectProperty.getClass().getCanonicalName()));
    }
}
