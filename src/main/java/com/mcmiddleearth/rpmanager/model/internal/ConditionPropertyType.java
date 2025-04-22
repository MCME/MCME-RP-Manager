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

import com.mcmiddleearth.rpmanager.model.item.condition.*;

import java.util.stream.Stream;

public enum ConditionPropertyType {
    BROKEN("minecraft:broken", BrokenConditionProperty.class),
    BUNDLE_HAS_SELECTED_ITEM("minecraft:bundle/has_selected_item", BundleHasSelectedItemConditionProperty.class),
    CARRIED("minecraft:carried", CarriedConditionProperty.class),
    COMPONENT("minecraft:component", ComponentConditionProperty.class, "predicate", "value"),
    DAMAGED("minecraft:damaged", DamagedConditionProperty.class),
    EXTENDED_VIEW("minecraft:extended_view", ExtendedViewConditionProperty.class),
    FISHING_ROD_CAST("minecraft:fishing_rod/cast", FishingRodCastConditionProperty.class),
    HAS_COMPONENT("minecraft:has_component", HasComponentConditionProperty.class, "component", "ignore_default"),
    KEYBIND_DOWN("minecraft:keybind_down", KeybindDownConditionProperty.class, "keybind"),
    SELECTED("minecraft:selected", SelectedConditionProperty.class),
    USING_ITEM("minecraft:using_item", UsingItemConditionProperty.class),
    VIEW_ENTITY("minecraft:view_entity", ViewEntityConditionProperty.class),
    CUSTOM_MODEL_DATA("minecraft:custom_model_data", CustomModelDataConditionProperty.class, "index"),
    ;

    private final String id;
    private final Class<? extends ConditionProperty> typeClass;
    private final String[] additionalFields;

    ConditionPropertyType(String id, Class<? extends ConditionProperty> typeClass, String... additionalFields) {
        this.id = id;
        this.typeClass = typeClass;
        this.additionalFields = additionalFields;
    }

    public String getId() {
        return id;
    }

    public Class<? extends ConditionProperty> getTypeClass() {
        return typeClass;
    }

    public String[] getAdditionalFields() {
        return additionalFields;
    }

    public static ConditionPropertyType byId(String id) {
        return Stream.of(values()).filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid condition property type: " + id));
    }

    public static ConditionPropertyType byConditionProperty(ConditionProperty conditionProperty) {
        return Stream.of(values()).filter(c -> c.getTypeClass().isAssignableFrom(conditionProperty.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid condition property type: " + conditionProperty.getClass().getCanonicalName()));
    }
}
