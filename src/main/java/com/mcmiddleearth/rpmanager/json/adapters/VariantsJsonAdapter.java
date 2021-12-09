package com.mcmiddleearth.rpmanager.json.adapters;

import com.google.gson.*;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.rpmanager.model.Model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VariantsJsonAdapter extends TypeAdapter<Map<String, List<Model>>> {
    public static class Factory implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != Map.class) {
                return null;
            }
            return (TypeAdapter<T>) new VariantsJsonAdapter(
                    gson.getAdapter(String.class), gson.getAdapter(Model.class));
        }
    }

    private final TypeAdapter<String> stringTypeAdapter;
    private final ListOrObjectJsonAdapter<Model> listOrObjectJsonAdapter;

    public VariantsJsonAdapter(TypeAdapter<String> stringTypeAdapter, TypeAdapter<Model> modelTypeAdapter) {
        this.stringTypeAdapter = stringTypeAdapter;
        this.listOrObjectJsonAdapter = new ListOrObjectJsonAdapter<>(modelTypeAdapter);
    }

    @Override
    public void write(JsonWriter out, Map<String, List<Model>> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        for (Map.Entry<String, List<Model>> entry : value.entrySet()) {
            out.name(String.valueOf(entry.getKey()));
            listOrObjectJsonAdapter.write(out, entry.getValue());
        }
        out.endObject();
    }

    @Override
    public Map<String, List<Model>> read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        Map<String, List<Model>> map = new LinkedHashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
            String key = stringTypeAdapter.read(in);
            List<Model> value = listOrObjectJsonAdapter.read(in);
            List<Model> replaced = map.put(key, value);
            if (replaced != null) {
                throw new JsonSyntaxException("duplicate key: " + key);
            }
        }
        in.endObject();
        return map;
    }
}
