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
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.rpmanager.model.internal.ItemsModelType;
import com.mcmiddleearth.rpmanager.model.item.ItemsModel;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemsModelListJsonAdapter extends TypeAdapter<List<ItemsModel>> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!List.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type.getType();
            Type actualType = parameterizedType.getActualTypeArguments()[0];
            if (!TypeToken.get(actualType).getRawType().isAssignableFrom(ItemsModel.class)) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new ItemsModelListJsonAdapter(new ItemsModelJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken),
                    Stream.of(ItemsModelType.values()).collect(Collectors.toMap(
                            ItemsModelType::getId, t -> adapt(gson.getAdapter(t.getTypeClass()))))));
        }

        private static <T extends ItemsModel> TypeAdapter<ItemsModel> adapt(TypeAdapter<T> typeAdapter) {
            return new TypeAdapter<>() {
                @SuppressWarnings("unchecked")
                @Override
                public void write(JsonWriter jsonWriter, ItemsModel itemsModel) throws IOException {
                    typeAdapter.write(jsonWriter, (T) itemsModel);
                }

                @Override
                public ItemsModel read(JsonReader jsonReader) throws IOException {
                    return typeAdapter.read(jsonReader);
                }
            };
        }
    }

    private final ItemsModelJsonAdapter itemsModelJsonAdapter;

    public ItemsModelListJsonAdapter(ItemsModelJsonAdapter itemsModelJsonAdapter) {
        this.itemsModelJsonAdapter = itemsModelJsonAdapter;
    }

    @Override
    public void write(JsonWriter jsonWriter, List<ItemsModel> itemsModels) throws IOException {
        if (itemsModels == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.beginArray();
            for (ItemsModel itemsModel : itemsModels) {
                itemsModelJsonAdapter.write(jsonWriter, itemsModel);
            }
            jsonWriter.endArray();
        }
    }

    @Override
    public List<ItemsModel> read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            List<ItemsModel> list = new LinkedList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                list.add(itemsModelJsonAdapter.read(jsonReader));
            }
            jsonReader.endArray();
            return list;
        }
    }
}
