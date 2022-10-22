/*
 * Copyright (C) 2022 MCME
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

import java.io.File;
import java.io.IOException;

public class FileJsonAdapter extends TypeAdapter<File> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return (TypeAdapter<T>) new FileJsonAdapter(gson.getAdapter(String.class));
        }
    }

    private final TypeAdapter<String> stringTypeAdapter;

    public FileJsonAdapter(TypeAdapter<String> stringTypeAdapter) {
        this.stringTypeAdapter = stringTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, File value) throws IOException {
        stringTypeAdapter.write(out, value.getAbsolutePath());
    }

    @Override
    public File read(JsonReader in) throws IOException {
        return new File(stringTypeAdapter.read(in));
    }
}
