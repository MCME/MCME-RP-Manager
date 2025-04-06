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

package com.mcmiddleearth.rpmanager.json.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.rpmanager.model.internal.ConditionPropertyType;
import com.mcmiddleearth.rpmanager.model.item.ConditionItemsModel;
import com.mcmiddleearth.rpmanager.model.item.ItemsModel;
import com.mcmiddleearth.rpmanager.model.item.condition.ConditionProperty;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConditionItemModelJsonAdapter extends TypeAdapter<ConditionItemsModel> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != ConditionItemsModel.class) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new ConditionItemModelJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken),
                    new ItemsModelJsonAdapter.Factory().create(gson, TypeToken.get(ItemsModel.class)),
                    Stream.of(ConditionPropertyType.values()).collect(Collectors.toMap(
                            ConditionPropertyType::getId, t -> adapt(gson.getAdapter(t.getTypeClass())))));
        }

        private static <T extends ConditionProperty> TypeAdapter<ConditionProperty> adapt(TypeAdapter<T> typeAdapter) {
            return new TypeAdapter<>() {
                @SuppressWarnings("unchecked")
                @Override
                public void write(JsonWriter jsonWriter, ConditionProperty conditionProperty) throws IOException {
                    typeAdapter.write(jsonWriter, (T) conditionProperty);
                }

                @Override
                public ConditionProperty read(JsonReader jsonReader) throws IOException {
                    return typeAdapter.read(jsonReader);
                }
            };
        }
    }

    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;
    private final TypeAdapter<ItemsModel> itemsModelJsonAdapter;
    private final Map<String, TypeAdapter<ConditionProperty>> conditionPropertyTypeAdapters;

    public ConditionItemModelJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter,
                                         TypeAdapter<ItemsModel> itemsModelJsonAdapter,
                                         Map<String, TypeAdapter<ConditionProperty>> conditionPropertyTypeAdapters) {
        this.mapTypeAdapter = mapTypeAdapter;
        this.itemsModelJsonAdapter = itemsModelJsonAdapter;
        this.conditionPropertyTypeAdapters = conditionPropertyTypeAdapters;
    }

    @Override
    public void write(JsonWriter jsonWriter, ConditionItemsModel conditionItemModel) throws IOException {
        if (conditionItemModel == null) {
            mapTypeAdapter.write(jsonWriter, null);
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            if (conditionItemModel.getProperty() != null) {
                ConditionPropertyType conditionPropertyType =
                        ConditionPropertyType.byConditionProperty(conditionItemModel.getProperty());
                result.put("property", conditionPropertyType.getId());
                result.putAll(mapTypeAdapter.fromJson(
                        conditionPropertyTypeAdapters.get(conditionPropertyType.getId()).toJson(
                                conditionItemModel.getProperty())));
            }
            if (conditionItemModel.getOnTrue() != null) {
                result.put("on_true", mapTypeAdapter.fromJson(
                        itemsModelJsonAdapter.toJson(conditionItemModel.getOnTrue())));
            }
            if (conditionItemModel.getOnFalse() != null) {
                result.put("on_false", mapTypeAdapter.fromJson(
                        itemsModelJsonAdapter.toJson(conditionItemModel.getOnFalse())));
            }
            mapTypeAdapter.write(jsonWriter, result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConditionItemsModel read(JsonReader jsonReader) throws IOException {
        Map<String, Object> map = mapTypeAdapter.read(jsonReader);
        if (map == null) {
            return null;
        }
        map = new HashMap<>(map);
        ConditionItemsModel conditionItemModel = new ConditionItemsModel();
        if (map.get("property") != null) {
            ConditionPropertyType conditionPropertyType = ConditionPropertyType.byId((String) map.remove("property"));
            Map<String, Object> partial = map.entrySet().stream()
                    .filter(e -> Stream.of(conditionPropertyType.getAdditionalFields())
                            .anyMatch(f -> f.equals(e.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            conditionItemModel.setProperty(conditionPropertyTypeAdapters.get(conditionPropertyType.getId()).fromJson(
                    mapTypeAdapter.toJson(partial)));
        }
        if (map.get("on_true") != null) {
            conditionItemModel.setOnTrue(itemsModelJsonAdapter.fromJson(
                    mapTypeAdapter.toJson((Map<String, Object>) map.get("on_true"))));
        }
        if (map.get("on_false") != null) {
            conditionItemModel.setOnFalse(itemsModelJsonAdapter.fromJson(
                    mapTypeAdapter.toJson((Map<String, Object>) map.get("on_false"))));
        }
        return conditionItemModel;
    }
}
