/*
 * Copyright (C) 2021 MCME
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
import com.mcmiddleearth.rpmanager.model.When;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WhenJsonAdapter extends TypeAdapter<When> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != When.class) {
                return null;
            }
            TypeToken<?> mapTypeToken = TypeToken.getParameterized(Map.class, String.class, Object.class);
            return (TypeAdapter<T>) new WhenJsonAdapter(
                    (TypeAdapter<Map<String, Object>>) gson.getAdapter(mapTypeToken));
        }
    }

    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;

    public WhenJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter) {
        this.mapTypeAdapter = mapTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, When value) throws IOException {
        if (value.getOR() != null) {
            mapTypeAdapter.write(out, Map.of("OR", value.getOR()));
        } else {
            mapTypeAdapter.write(out, value.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public When read(JsonReader in) throws IOException {
        When when = new When();
        Map<String, Object> map = mapTypeAdapter.read(in);
        if (map != null && map.containsKey("OR")) {
            when.setOR((List<Map<String, Object>>) map.get("OR"));
        } else {
            when.setValue(map);
        }
        return when;
    }
}
