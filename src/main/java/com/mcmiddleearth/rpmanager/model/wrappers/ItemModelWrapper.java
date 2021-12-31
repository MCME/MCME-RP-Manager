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

package com.mcmiddleearth.rpmanager.model.wrappers;

import com.mcmiddleearth.rpmanager.model.ItemModel;

public class ItemModelWrapper extends ModelWrapper<ItemModel> {
    public ItemModelWrapper(String filePath, ModelData<ItemModel> current, ModelData<ItemModel> urps,
                            ModelData<ItemModel> vanilla) {
        super(filePath, current, urps, vanilla);
    }
}
