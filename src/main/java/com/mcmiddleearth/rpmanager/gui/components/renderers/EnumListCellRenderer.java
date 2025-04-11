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

package com.mcmiddleearth.rpmanager.gui.components.renderers;

import com.google.gson.annotations.SerializedName;

import javax.swing.*;
import java.awt.*;

public class EnumListCellRenderer<T extends Enum<T>> extends DefaultListCellRenderer {
    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ((DefaultListCellRenderer) c).setText(getLabel((T) value));
        return c;
    }

    private String getLabel(T element) {
        if (element == null) {
            return "";
        } else {
            try {
                return element.getClass().getField(element.name()).getAnnotation(SerializedName.class).value();
            } catch (NoSuchFieldException e) {
                // should never happen
                return "";
            }
        }
    }
}
