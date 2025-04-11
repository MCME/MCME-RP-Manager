/*
 * Copyright (C) 2024 MCME
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

package com.mcmiddleearth.rpmanager.events;

public class ListSelectionChangeEvent implements Event {
    private final Object source;
    private final Object object;

    public ListSelectionChangeEvent(Object source, Object object) {
        this.source = source;
        this.object = object;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public Object getObject() {
        return object;
    }
}
