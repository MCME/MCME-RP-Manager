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

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.rpmanager.model.internal.TintType;
import com.mcmiddleearth.rpmanager.model.item.tint.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TintJsonAdapter extends TypeAdapter<Tint> {
    private final TypeAdapter<Map<String, Object>> mapTypeAdapter;
    private final Map<String, TypeAdapter<Tint>> tintTypeAdapters;

    public TintJsonAdapter(TypeAdapter<Map<String, Object>> mapTypeAdapter,
                           Map<String, TypeAdapter<Tint>> tintTypeAdapters) {
        this.mapTypeAdapter = mapTypeAdapter;
        this.tintTypeAdapters = tintTypeAdapters;
    }

    @Override
    public void write(JsonWriter jsonWriter, Tint tint) throws IOException {
        if (tint == null) {
            mapTypeAdapter.write(jsonWriter, null);
        } else {
            TintType tintType = TintType.byTint(tint);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("type", tintType.getId());
            result.putAll(mapTypeAdapter.fromJson(tintTypeAdapters.get(tintType.getId()).toJson(tint)));
            mapTypeAdapter.write(jsonWriter, result);
        }
    }

    @Override
    public Tint read(JsonReader jsonReader) throws IOException {
        Map<String, Object> map = mapTypeAdapter.read(jsonReader);
        if (map == null) {
            return null;
        }
        map = new HashMap<>(map);
        TintType tintType = TintType.byId((String) map.remove("type"));
        return tintTypeAdapters.get(tintType.getId()).fromJson(mapTypeAdapter.toJson(map));
    }
}
