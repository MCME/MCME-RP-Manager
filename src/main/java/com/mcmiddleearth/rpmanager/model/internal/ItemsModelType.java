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

import com.mcmiddleearth.rpmanager.model.item.*;

import java.util.stream.Stream;

public enum ItemsModelType {
    MODEL("minecraft:model", ModelItemsModel.class),
    COMPOSITE("minecraft:composite", CompositeItemsModel.class),
    CONDITION("minecraft:condition", ConditionItemsModel.class),
    SELECT("minecraft:select", SelectItemsModel.class),
    RANGE_DISPATCH("minecraft:range_dispatch", RangeDispatchItemsModel.class),
    EMPTY("minecraft:empty", EmptyItemsModel.class),
    BUNDLE_SELECTED_ITEM("minecraft:bundle/selected_item", BundleSelectedItemItemsModel.class),
    SPECIAL("minecraft:special", SpecialItemsModel.class),
    ;

    private final String id;
    private final Class<? extends ItemsModel> typeClass;

    ItemsModelType(String id, Class<? extends ItemsModel> typeClass) {
        this.id = id;
        this.typeClass = typeClass;
    }

    public String getId() {
        return id;
    }

    public Class<? extends ItemsModel> getTypeClass() {
        return typeClass;
    }

    public static ItemsModelType byId(String id) {
        return Stream.of(values()).filter(i -> i.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid item model type: " + id));
    }

    public static ItemsModelType byItemsModel(ItemsModel itemsModel) {
        return Stream.of(values()).filter(i -> i.getTypeClass().isAssignableFrom(itemsModel.getClass()))
                .findFirst().orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid item model type: " + itemsModel.getClass().getCanonicalName()));
    }
}
