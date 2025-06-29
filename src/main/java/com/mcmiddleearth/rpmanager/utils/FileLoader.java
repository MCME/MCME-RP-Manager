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

import com.mcmiddleearth.rpmanager.gui.components.tree.StaticTreeNode;
import com.mcmiddleearth.rpmanager.gui.utils.TreeUtils;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.loaders.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public interface FileLoader {
    List<FileLoader> LOADERS = Arrays.asList(
            new BlockstateFileLoader(), new ItemFileLoader(), new BlockModelFileLoader(), new ItemModelFileLoader(),
            new TextureFileLoader(), new TextFileLoader());

    Object loadFile(Layer layer, Object[] path) throws IOException;
    Object loadFile(File file) throws IOException;
    boolean canLoad(Layer layer, Object[] path);
    boolean canLoad(File file) throws IOException;

    static SelectedFileData load(Layer layer, Object[] path) throws IOException {
        for (FileLoader loader : LOADERS) {
            if (loader.canLoad(layer, path)) {
                String fileName = path[path.length-1].toString();
                return new SelectedFileData(
                        loader.loadFile(layer, path), fileName.substring(0, fileName.lastIndexOf(".")), path);
            }
        }
        return null;
    }

    static SelectedFileData load(StaticTreeNode node) throws IOException {
        for (FileLoader loader : LOADERS) {
            if (loader.canLoad(node.getFile())) {
                String fileName = node.getFile().getName();
                return new SelectedFileData(
                        loader.loadFile(node.getFile()), fileName.substring(0, fileName.lastIndexOf(".")),
                        TreeUtils.getPathForNode(node).getPath());
            }
        }
        return null;
    }
}
