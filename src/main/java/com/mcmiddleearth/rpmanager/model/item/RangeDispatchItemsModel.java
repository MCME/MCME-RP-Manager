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

package com.mcmiddleearth.rpmanager.model.item;

import com.google.gson.annotations.JsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.ItemsModelJsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.RangeDispatchItemModelJsonAdapter;
import com.mcmiddleearth.rpmanager.model.item.range.RangeProperty;

import java.util.List;

@JsonAdapter(RangeDispatchItemModelJsonAdapter.Factory.class)
public class RangeDispatchItemsModel extends ItemsModel {
    private RangeProperty property;
    private Float scale;
    private List<Entry> entries;
    private ItemsModel fallback;

    public RangeProperty getProperty() {
        return property;
    }

    public void setProperty(RangeProperty property) {
        this.property = property;
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scale) {
        this.scale = scale;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public ItemsModel getFallback() {
        return fallback;
    }

    public void setFallback(ItemsModel fallback) {
        this.fallback = fallback;
    }

    public static class Entry {
        private float threshold;
        @JsonAdapter(ItemsModelJsonAdapter.Factory.class)
        private ItemsModel model;

        public float getThreshold() {
            return threshold;
        }

        public void setThreshold(float threshold) {
            this.threshold = threshold;
        }

        public ItemsModel getModel() {
            return model;
        }

        public void setModel(ItemsModel model) {
            this.model = model;
        }
    }
}
