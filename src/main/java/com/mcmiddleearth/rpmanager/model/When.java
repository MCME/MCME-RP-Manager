/*
 * Copyright (C) 2021 MCME
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
