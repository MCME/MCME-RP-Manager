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
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ListOrObjectJsonAdapter<E> extends TypeAdapter<List<E>> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != List.class) {
                return null;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type.getType();
            Type actualType = parameterizedType.getActualTypeArguments()[0];
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
            return new ListOrObjectJsonAdapter(adapter);
        }
    }

    private final TypeAdapter<E> adapter;

    public ListOrObjectJsonAdapter(TypeAdapter<E> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void write(JsonWriter out, List<E> value) throws IOException {
        if (value.size() == 1) {
            out.beginObject();
            adapter.write(out, value.get(0));
            out.endObject();
        } else {
            out.beginArray();
            for (E e : value) {
                adapter.write(out, e);
            }
            out.endArray();
        }
    }

    @Override
    public List<E> read(JsonReader in) throws IOException {
        List<E> result = new LinkedList<>();
        if (in.peek() != JsonToken.BEGIN_ARRAY) {
            result.add(adapter.read(in));
        } else {
            in.beginArray();
            while (in.peek() != JsonToken.END_ARRAY) {
                result.add(adapter.read(in));
            }
            in.endArray();
        }
        return result;
    }
}
