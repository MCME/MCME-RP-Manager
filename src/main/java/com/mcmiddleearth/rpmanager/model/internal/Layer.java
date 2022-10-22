package com.mcmiddleearth.rpmanager.model.internal;

import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.ItemModel;

import java.awt.image.BufferedImage;
import java.util.Map;

public class Layer {
    private Map<String, BlockState> blockStates;
    private Map<String, BlockModel> blockModels;
    private Map<String, ItemModel> itemModels;
    private Map<String, BufferedImage> textures;

    public Map<String, BlockState> getBlockStates() {
        return blockStates;
    }

    public void setBlockStates(Map<String, BlockState> blockStates) {
        this.blockStates = blockStates;
    }

    public Map<String, BlockModel> getBlockModels() {
        return blockModels;
    }

    public void setBlockModels(Map<String, BlockModel> blockModels) {
        this.blockModels = blockModels;
    }

    public Map<String, ItemModel> getItemModels() {
        return itemModels;
    }

    public void setItemModels(Map<String, ItemModel> itemModels) {
        this.itemModels = itemModels;
    }

    public Map<String, BufferedImage> getTextures() {
        return textures;
    }

    public void setTextures(Map<String, BufferedImage> textures) {
        this.textures = textures;
    }
}
