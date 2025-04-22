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
import com.mcmiddleearth.rpmanager.json.adapters.ConditionItemModelJsonAdapter;
import com.mcmiddleearth.rpmanager.model.item.condition.ConditionProperty;

@JsonAdapter(ConditionItemModelJsonAdapter.Factory.class)
public class ConditionItemsModel extends ItemsModel {
    private ConditionProperty property;
    private ItemsModel onTrue;
    private ItemsModel onFalse;

    public ConditionProperty getProperty() {
        return property;
    }

    public void setProperty(ConditionProperty property) {
        this.property = property;
    }

    public ItemsModel getOnTrue() {
        return onTrue;
    }

    public void setOnTrue(ItemsModel onTrue) {
        this.onTrue = onTrue;
    }

    public ItemsModel getOnFalse() {
        return onFalse;
    }

    public void setOnFalse(ItemsModel onFalse) {
        this.onFalse = onFalse;
    }
}
