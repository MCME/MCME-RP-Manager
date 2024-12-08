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

package com.mcmiddleearth.rpmanager.utils.loaders;

import com.mcmiddleearth.rpmanager.model.project.Layer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class TextFileLoader extends AbstractFileLoader {
    private static final List<String> SUPPORTED_EXTENSIONS = List.of(
            ".json",
            ".mtl",
            ".obj",
            ".mcmeta",
            ".yml",
            ".yaml",
            ".glsl",
            ".fsh",
            ".vsh",
            ".proprieties",
            ".txt");

    @Override
    public Object loadFile(Layer layer, Object[] path) throws IOException {
        return new String(loadBytes(layer, path), StandardCharsets.UTF_8);
    }

    @Override
    public Object loadFile(File file) throws IOException {
        return new String(loadBytesFromFile(file, Collections.emptyList()), StandardCharsets.UTF_8);
    }

    @Override
    public boolean canLoad(Layer layer, Object[] path) {
        return path != null && path.length > 0 && hasSupportedExtension(path[path.length - 1].toString());
    }

    @Override
    public boolean canLoad(File file) {
        return hasSupportedExtension(file.getName());
    }

    private static boolean hasSupportedExtension(String fileName) {
        return SUPPORTED_EXTENSIONS.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));
    }
}
