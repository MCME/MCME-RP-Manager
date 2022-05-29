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

package com.mcmiddleearth.rpmanager.gui.components.tree;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class ResourcePackTreeFactory {
    private ResourcePackTreeFactory() {}

    public static StaticTreeNode createRootNode(File resourcePackMeta) {
        return createNode(null, resourcePackMeta.getParentFile());
    }

    private static StaticTreeNode createNode(StaticTreeNode parent, File file) {
        StaticTreeNode node = new StaticTreeNode(parent, file.getName(), new LinkedList<>());
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(f -> node.getChildren().add(createNode(node, f)));
        }
        return node;
    }
}
