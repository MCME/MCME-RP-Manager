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

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class TextureFileLoader extends AbstractFileLoader {
    @Override
    public Object loadFile(Layer layer, Object[] path) throws IOException {
        byte[] content = loadBytes(layer, path);
        return ImageIO.read(new ByteArrayInputStream(content));
    }

    @Override
    public Object loadFile(File file) throws IOException {
        byte[] content = loadBytesFromFile(file, Collections.emptyList());
        return ImageIO.read(new ByteArrayInputStream(content));
    }

    @Override
    public boolean canLoad(Layer layer, Object[] path) {
        return path != null && path.length > 0 && path[path.length - 1].toString().endsWith(".png") &&
                isTexturePath(Arrays.stream(path).map(Object::toString)
                        .skip(layer.getFile().getName().endsWith(".jar") ? 0L : 1L)
                        .collect(Collectors.joining("/")));
    }

    @Override
    public boolean canLoad(File file) {
        return contains(file.toPath(), Path.of("assets", "minecraft", "textures"));
    }

    private static boolean isTexturePath(String path) {
        return path.startsWith("assets/minecraft/textures/");
    }
}
