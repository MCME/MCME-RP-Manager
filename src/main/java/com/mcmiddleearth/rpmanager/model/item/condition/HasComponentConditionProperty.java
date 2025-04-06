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

package com.mcmiddleearth.rpmanager.model.item.condition;

import com.google.gson.annotations.SerializedName;

public class HasComponentConditionProperty extends ConditionProperty {
    private String component;
    @SerializedName("ignore_default")
    private Boolean ignoreDefault;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Boolean getIgnoreDefault() {
        return ignoreDefault;
    }

    public void setIgnoreDefault(Boolean ignoreDefault) {
        this.ignoreDefault = ignoreDefault;
    }
}
