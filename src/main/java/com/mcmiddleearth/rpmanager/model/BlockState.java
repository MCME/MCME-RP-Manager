package com.mcmiddleearth.rpmanager.model;

import com.google.gson.annotations.JsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.VariantsJsonAdapter;

import java.util.List;
import java.util.Map;

public class BlockState {
    private String filePath;
    @JsonAdapter(VariantsJsonAdapter.Factory.class)
    private Map<String, List<Model>> variants;
    private List<Case> multipart;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, List<Model>> getVariants() {
        return variants;
    }

    public void setVariants(Map<String, List<Model>> variants) {
        this.variants = variants;
    }

    public List<Case> getMultipart() {
        return multipart;
    }

    public void setMultipart(List<Case> multipart) {
        this.multipart = multipart;
    }
}
