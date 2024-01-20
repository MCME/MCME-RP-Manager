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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class ResourcePackTreeFactory {
    private ResourcePackTreeFactory() {}

    public static StaticTreeNode createRootNode(File resourcePackMeta) throws IOException, GitAPIException {
        File gitDir = new File(resourcePackMeta.getParentFile(), ".git");
        Git git = null;
        if (gitDir.exists() && gitDir.isDirectory()) {
            git = Git.open(resourcePackMeta.getParentFile());
        }
        StaticTreeNode node = createNode(null, resourcePackMeta.getParentFile(), git);
        node.refreshGitStatus();
        return node;
    }

    public static StaticTreeNode createNode(StaticTreeNode parent, File file, Git git)
            throws IOException, GitAPIException {
        StaticTreeNode node = new StaticTreeNode(parent, file.getName(), file, file.isDirectory(), new LinkedList<>());
        node.setGit(git);
        if (file.isDirectory()) {
            for (File f : Arrays.stream(file.listFiles()).sorted(Comparator.comparing(File::getName)).toList()) {
                node.getChildren().add(createNode(node, f, git));
            }
        }
        return node;
    }
}
