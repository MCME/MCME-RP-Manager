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
import com.mcmiddleearth.rpmanager.json.adapters.ListOrObjectJsonAdapter;
import com.mcmiddleearth.rpmanager.json.adapters.SelectItemModelJsonAdapter;
import com.mcmiddleearth.rpmanager.model.item.select.SelectProperty;

import java.util.List;

@JsonAdapter(SelectItemModelJsonAdapter.Factory.class)
public class SelectItemsModel extends ItemsModel {
    private SelectProperty property;
    private List<Case> cases;
    private ItemsModel fallback;

    public SelectProperty getProperty() {
        return property;
    }

    public void setProperty(SelectProperty property) {
        this.property = property;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    public ItemsModel getFallback() {
        return fallback;
    }

    public void setFallback(ItemsModel fallback) {
        this.fallback = fallback;
    }

    public static class Case {
        @JsonAdapter(ListOrObjectJsonAdapter.Factory.class)
        private List<String> when;
        @JsonAdapter(ItemsModelJsonAdapter.Factory.class)
        private ItemsModel model;

        public List<String> getWhen() {
            return when;
        }

        public void setWhen(List<String> when) {
            this.when = when;
        }

        public ItemsModel getModel() {
            return model;
        }

        public void setModel(ItemsModel model) {
            this.model = model;
        }
    }
}
