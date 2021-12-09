package com.mcmiddleearth.rpmanager.model;

import com.google.gson.annotations.JsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.ListOrObjectJsonAdapter;

import java.util.List;

public class Case {
    private When when;
    @JsonAdapter(ListOrObjectJsonAdapter.Factory.class)
    private List<Model> apply;

    public When getWhen() {
        return when;
    }

    public void setWhen(When when) {
        this.when = when;
    }

    public List<Model> getApply() {
        return apply;
    }

    public void setApply(List<Model> apply) {
        this.apply = apply;
    }
}
