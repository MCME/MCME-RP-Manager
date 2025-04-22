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
import com.mcmiddleearth.rpmanager.model.RGB;

import java.io.IOException;

public class RGBJsonAdapter extends TypeAdapter<RGB> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != RGB.class) {
                return null;
            }
            return (TypeAdapter<T>) new RGBJsonAdapter(gson.getAdapter(Integer.class), gson.getAdapter(float[].class));
        }
    }

    private final TypeAdapter<Integer> integerTypeAdapter;
    private final TypeAdapter<float[]> floatArrayTypeAdapter;

    public RGBJsonAdapter(TypeAdapter<Integer> integerTypeAdapter, TypeAdapter<float[]> floatArrayTypeAdapter) {
        this.integerTypeAdapter = integerTypeAdapter;
        this.floatArrayTypeAdapter = floatArrayTypeAdapter;
    }

    @Override
    public void write(JsonWriter jsonWriter, RGB rgb) throws IOException {
        if (rgb.getIntValue() != null) {
            integerTypeAdapter.write(jsonWriter, rgb.getIntValue());
        } else if (rgb.getFloatValues() != null) {
            floatArrayTypeAdapter.write(jsonWriter, rgb.getFloatValues());
        } else {
            throw new IllegalStateException("Invalid RGB value");
        }
    }

    @Override
    public RGB read(JsonReader jsonReader) throws IOException {
        RGB rgb = new RGB();
        if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
            rgb.setFloatValues(floatArrayTypeAdapter.read(jsonReader));
        } else {
            rgb.setIntValue(integerTypeAdapter.read(jsonReader));
        }
        return rgb;
    }
}
