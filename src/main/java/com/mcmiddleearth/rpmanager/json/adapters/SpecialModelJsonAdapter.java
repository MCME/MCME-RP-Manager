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
import com.mcmiddleearth.rpmanager.model.internal.SpecialModelType;
import com.mcmiddleearth.rpmanager.model.item.special.SpecialModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecialModelJsonAdapter extends TypeAdapter<SpecialModel> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!SpecialModel.class.isAssignableFrom(type.getRawType())) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new SpecialModelJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken),
                    Stream.of(SpecialModelType.values()).collect(Collectors.toMap(
                            SpecialModelType::getId, t -> adapt(gson.getAdapter(t.getTypeClass())))));
        }

        private static <T extends SpecialModel> TypeAdapter<SpecialModel> adapt(TypeAdapter<T> typeAdapter) {
            return new TypeAdapter<>() {
                @SuppressWarnings("unchecked")
                @Override
                public void write(JsonWriter jsonWriter, SpecialModel specialModel) throws IOException {
                    typeAdapter.write(jsonWriter, (T) specialModel);
                }

                @Override
                public SpecialModel read(JsonReader jsonReader) throws IOException {
                    return typeAdapter.read(jsonReader);
                }
            };
        }
    }

    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;
    private final Map<String, TypeAdapter<SpecialModel>> specialModelTypeAdapters;

    public SpecialModelJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter,
                                   Map<String, TypeAdapter<SpecialModel>> specialModelTypeAdapters) {
        this.mapTypeAdapter = mapTypeAdapter;
        this.specialModelTypeAdapters = specialModelTypeAdapters;
    }

    @Override
    public void write(JsonWriter jsonWriter, SpecialModel specialModel) throws IOException {
        if (specialModel == null) {
            mapTypeAdapter.write(jsonWriter, null);
        } else {
            SpecialModelType specialModelType = SpecialModelType.bySpecialModel(specialModel);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("type", specialModelType.getId());
            result.putAll(mapTypeAdapter.fromJson(specialModelTypeAdapters.get(specialModelType.getId())
                    .toJson(specialModel)));
            mapTypeAdapter.write(jsonWriter, result);
        }
    }

    @Override
    public SpecialModel read(JsonReader jsonReader) throws IOException {
        Map<String, Object> map = mapTypeAdapter.read(jsonReader);
        if (map == null) {
            return null;
        }
        map = new HashMap<>(map);
        SpecialModelType specialModelType = SpecialModelType.byId((String) map.remove("type"));
        return specialModelTypeAdapters.get(specialModelType.getId()).fromJson(mapTypeAdapter.toJson(map));
    }
}
