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
import com.mcmiddleearth.rpmanager.json.adapters.SpecialModelJsonAdapter;
import com.mcmiddleearth.rpmanager.model.item.special.SpecialModel;

public class SpecialItemsModel extends ItemsModel {
    @JsonAdapter(SpecialModelJsonAdapter.Factory.class)
    private SpecialModel model;
    private String base;

    public SpecialModel getModel() {
        return model;
    }

    public void setModel(SpecialModel model) {
        this.model = model;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
