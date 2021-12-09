package com.mcmiddleearth.rpmanager.model;

import com.google.gson.annotations.JsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.WhenJsonAdapter;

import java.util.List;
import java.util.Map;

@JsonAdapter(WhenJsonAdapter.Factory.class)
public class When {
    private Map<String, Object> value;
    private List<Map<String, Object>> OR;

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public List<Map<String, Object>> getOR() {
        return OR;
    }

    public void setOR(List<Map<String, Object>> OR) {
        this.OR = OR;
    }
}
