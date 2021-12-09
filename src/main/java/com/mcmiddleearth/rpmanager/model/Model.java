package com.mcmiddleearth.rpmanager.model;

public class Model {
    private String model;
    private Integer x;
    private Integer y;
    private Boolean uvlock;
    private Integer weight;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Boolean getUvlock() {
        return uvlock;
    }

    public void setUvlock(Boolean uvlock) {
        this.uvlock = uvlock;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
