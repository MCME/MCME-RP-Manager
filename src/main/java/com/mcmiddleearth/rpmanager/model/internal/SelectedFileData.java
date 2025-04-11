/*
 * Copyright (C) 2022 MCME
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

package com.mcmiddleearth.rpmanager.model.internal;

public class SelectedFileData {
    private final Object data;
    private final String name;
    private final Object[] path;

    public SelectedFileData(Object data, String name, Object[] path) {
        this.data = data;
        this.name = name;
        this.path = path;
    }

    public Object getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public Object[] getPath() {
        return path;
    }
}
