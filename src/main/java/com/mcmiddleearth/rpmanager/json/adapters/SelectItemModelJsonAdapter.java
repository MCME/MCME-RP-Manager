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
import com.mcmiddleearth.rpmanager.model.internal.SelectPropertyType;
import com.mcmiddleearth.rpmanager.model.item.ItemsModel;
import com.mcmiddleearth.rpmanager.model.item.SelectItemsModel;
import com.mcmiddleearth.rpmanager.model.item.select.SelectProperty;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectItemModelJsonAdapter extends TypeAdapter<SelectItemsModel> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != SelectItemsModel.class) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new SelectItemModelJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken),
                    gson.getAdapter(SelectItemsModel.Case.class),
                    new ItemsModelJsonAdapter.Factory().create(gson, TypeToken.get(ItemsModel.class)),
                    Stream.of(SelectPropertyType.values()).collect(Collectors.toMap(
                            SelectPropertyType::getId, t -> adapt(gson.getAdapter(t.getTypeClass())))));
        }

        private static <T extends SelectProperty> TypeAdapter<SelectProperty> adapt(TypeAdapter<T> typeAdapter) {
            return new TypeAdapter<>() {
                @SuppressWarnings("unchecked")
                @Override
                public void write(JsonWriter jsonWriter, SelectProperty selectProperty) throws IOException {
                    typeAdapter.write(jsonWriter, (T) selectProperty);
                }

                @Override
                public SelectProperty read(JsonReader jsonReader) throws IOException {
                    return typeAdapter.read(jsonReader);
                }
            };
        }
    }

    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;
    private final TypeAdapter<SelectItemsModel.Case> caseTypeAdapter;
    private final TypeAdapter<ItemsModel> itemsModelJsonAdapter;
    private final Map<String, TypeAdapter<SelectProperty>> selectPropertyTypeAdapters;

    public SelectItemModelJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter,
                                      TypeAdapter<SelectItemsModel.Case> caseTypeAdapter,
                                      TypeAdapter<ItemsModel> itemsModelJsonAdapter,
                                      Map<String, TypeAdapter<SelectProperty>> selectPropertyTypeAdapters) {
        this.mapTypeAdapter = mapTypeAdapter;
        this.caseTypeAdapter = caseTypeAdapter;
        this.itemsModelJsonAdapter = itemsModelJsonAdapter;
        this.selectPropertyTypeAdapters = selectPropertyTypeAdapters;
    }

    @Override
    public void write(JsonWriter jsonWriter, SelectItemsModel selectItemModel) throws IOException {
        if (selectItemModel == null) {
            mapTypeAdapter.write(jsonWriter, null);
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            if (selectItemModel.getProperty() != null) {
                SelectPropertyType selectPropertyType =
                        SelectPropertyType.bySelectProperty(selectItemModel.getProperty());
                result.put("property", selectPropertyType.getId());
                result.putAll(mapTypeAdapter.fromJson(
                        selectPropertyTypeAdapters.get(selectPropertyType.getId()).toJson(
                                selectItemModel.getProperty())));
            }
            if (selectItemModel.getCases() != null) {
                List<Map<String, Object>> list = new LinkedList<>();
                for (SelectItemsModel.Case c : selectItemModel.getCases()) {
                    list.add(mapTypeAdapter.fromJson(caseTypeAdapter.toJson(c)));
                }
                result.put("cases", list);
            }
            if (selectItemModel.getFallback() != null) {
                result.put("fallback", mapTypeAdapter.fromJson(
                        itemsModelJsonAdapter.toJson(selectItemModel.getFallback())));
            }
            mapTypeAdapter.write(jsonWriter, result);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectItemsModel read(JsonReader jsonReader) throws IOException {
        Map<String, Object> map = mapTypeAdapter.read(jsonReader);
        if (map == null) {
            return null;
        }
        map = new HashMap<>(map);
        SelectItemsModel selectItemModel = new SelectItemsModel();
        if (map.get("property") != null) {
            SelectPropertyType selectPropertyType = SelectPropertyType.byId((String) map.remove("property"));
            Map<String, Object> partial = map.entrySet().stream()
                    .filter(e -> Stream.of(selectPropertyType.getAdditionalFields())
                            .anyMatch(f -> f.equals(e.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            selectItemModel.setProperty(selectPropertyTypeAdapters.get(selectPropertyType.getId()).fromJson(
                    mapTypeAdapter.toJson(partial)));
        }
        if (map.get("cases") != null) {
            List<SelectItemsModel.Case> cases = new LinkedList<>();
            for (Map<String, Object> c : (List<Map<String, Object>>) map.get("cases")) {
                cases.add(caseTypeAdapter.fromJson(mapTypeAdapter.toJson(c)));
            }
            selectItemModel.setCases(cases);
        }
        if (map.get("fallback") != null) {
            selectItemModel.setFallback(itemsModelJsonAdapter.fromJson(
                    mapTypeAdapter.toJson((Map<String, Object>) map.get("fallback"))));
        }
        return selectItemModel;
    }
}
