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
import com.mcmiddleearth.rpmanager.model.internal.RangePropertyType;
import com.mcmiddleearth.rpmanager.model.item.ItemsModel;
import com.mcmiddleearth.rpmanager.model.item.RangeDispatchItemsModel;
import com.mcmiddleearth.rpmanager.model.item.range.RangeProperty;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RangeDispatchItemModelJsonAdapter extends TypeAdapter<RangeDispatchItemsModel> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != RangeDispatchItemsModel.class) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new RangeDispatchItemModelJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken),
                    gson.getAdapter(RangeDispatchItemsModel.Entry.class),
                    new ItemsModelJsonAdapter.Factory().create(gson, TypeToken.get(ItemsModel.class)),
                    Stream.of(RangePropertyType.values()).collect(Collectors.toMap(
                            RangePropertyType::getId, r -> adapt(gson.getAdapter(r.getTypeClass())))));
        }

        private static <T extends RangeProperty> TypeAdapter<RangeProperty> adapt(TypeAdapter<T> typeAdapter) {
            return new TypeAdapter<>() {
                @SuppressWarnings("unchecked")
                @Override
                public void write(JsonWriter jsonWriter, RangeProperty rangeProperty) throws IOException {
                    typeAdapter.write(jsonWriter, (T) rangeProperty);
                }

                @Override
                public RangeProperty read(JsonReader jsonReader) throws IOException {
                    return typeAdapter.read(jsonReader);
                }
            };
        }
    }

    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;
    private final TypeAdapter<RangeDispatchItemsModel.Entry> entryTypeAdapter;
    private final TypeAdapter<ItemsModel> itemsModelJsonAdapter;
    private final Map<String, TypeAdapter<RangeProperty>> rangePropertyTypeAdapters;

    public RangeDispatchItemModelJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter,
                                             TypeAdapter<RangeDispatchItemsModel.Entry> entryTypeAdapter,
                                             TypeAdapter<ItemsModel> itemsModelJsonAdapter,
                                             Map<String, TypeAdapter<RangeProperty>> rangePropertyTypeAdapters) {
        this.mapTypeAdapter = mapTypeAdapter;
        this.entryTypeAdapter = entryTypeAdapter;
        this.itemsModelJsonAdapter = itemsModelJsonAdapter;
        this.rangePropertyTypeAdapters = rangePropertyTypeAdapters;
    }

    @Override
    public void write(JsonWriter jsonWriter, RangeDispatchItemsModel rangeDispatchItemModel) throws IOException {
        if (rangeDispatchItemModel == null) {
            mapTypeAdapter.write(jsonWriter, null);
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            if (rangeDispatchItemModel.getProperty() != null) {
                RangePropertyType rangePropertyType =
                        RangePropertyType.byRangeProperty(rangeDispatchItemModel.getProperty());
                result.put("property", rangePropertyType.getId());
                result.putAll(mapTypeAdapter.fromJson(
                        rangePropertyTypeAdapters.get(rangePropertyType.getId()).toJson(
                                rangeDispatchItemModel.getProperty())));
            }
            result.put("scale", rangeDispatchItemModel.getScale());
            if (rangeDispatchItemModel.getEntries() != null) {
                List<Map<String, Object>> list = new LinkedList<>();
                for (RangeDispatchItemsModel.Entry entry : rangeDispatchItemModel.getEntries()) {
                    list.add(mapTypeAdapter.fromJson(entryTypeAdapter.toJson(entry)));
                }
                result.put("entries", list);
            }
            if (rangeDispatchItemModel.getFallback() != null) {
                result.put("fallback", mapTypeAdapter.fromJson(
                        itemsModelJsonAdapter.toJson(rangeDispatchItemModel.getFallback())));
            }
            mapTypeAdapter.write(jsonWriter, result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public RangeDispatchItemsModel read(JsonReader jsonReader) throws IOException {
        Map<String, Object> map = mapTypeAdapter.read(jsonReader);
        if (map == null) {
            return null;
        }
        map = new HashMap<>(map);
        RangeDispatchItemsModel rangeDispatchItemModel = new RangeDispatchItemsModel();
        if (map.get("property") != null) {
            RangePropertyType rangePropertyType = RangePropertyType.byId((String) map.remove("property"));
            Map<String, Object> partial = map.entrySet().stream()
                    .filter(e -> Stream.of(rangePropertyType.getAdditionalFields())
                            .anyMatch(f -> f.equals(e.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            rangeDispatchItemModel.setProperty(rangePropertyTypeAdapters.get(rangePropertyType.getId()).fromJson(
                    mapTypeAdapter.toJson(partial)));
        }
        if (map.get("scale") != null) {
            rangeDispatchItemModel.setScale(((Number) map.get("scale")).floatValue());
        }
        if (map.get("entries") != null) {
            List<RangeDispatchItemsModel.Entry> entries = new LinkedList<>();
            for (Map<String, Object> entry : (List<Map<String, Object>>) map.get("entries")) {
                entries.add(entryTypeAdapter.fromJson(mapTypeAdapter.toJson(entry)));
            }
            rangeDispatchItemModel.setEntries(entries);
        }
        if (map.get("fallback") != null) {
            rangeDispatchItemModel.setFallback(itemsModelJsonAdapter.fromJson(
                    mapTypeAdapter.toJson((Map<String, Object>) map.get("fallback"))));
        }
        return rangeDispatchItemModel;
    }
}
