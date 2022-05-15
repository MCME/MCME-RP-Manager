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

package com.mcmiddleearth.rpmanager.utils;

import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.loaders.BlockModelFileLoader;
import com.mcmiddleearth.rpmanager.utils.loaders.BlockstateFileLoader;
import com.mcmiddleearth.rpmanager.utils.loaders.ItemModelFileLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public interface JsonFileLoader {
    List<JsonFileLoader> LOADERS = Arrays.asList(
            new BlockstateFileLoader(), new BlockModelFileLoader(), new ItemModelFileLoader());

    Object loadFile(Layer layer, Object[] path) throws IOException;
    boolean canLoad(Layer layer, Object[] path);

    static Object load(Layer layer, Object[] path) throws IOException {
        for (JsonFileLoader loader : LOADERS) {
            if (loader.canLoad(layer, path)) {
                return loader.loadFile(layer, path);
            }
        }
        return null;
    }
}
