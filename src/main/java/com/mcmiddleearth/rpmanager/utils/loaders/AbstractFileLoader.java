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

package com.mcmiddleearth.rpmanager.utils.loaders;

import com.google.gson.Gson;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.utils.JsonFileLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class AbstractFileLoader implements JsonFileLoader {
    private final Gson gson = new Gson();

    protected Object loadFile(Layer layer, Object[] path, Class<?> resultClass) throws IOException {
        byte[] content = loadBytes(layer, path);
        try (InputStream is = new ByteArrayInputStream(content);
             Reader reader = new InputStreamReader(is)) {
            return gson.fromJson(reader, resultClass);
        }
    }

    protected Object loadFile(File file, Class<?> resultClass) throws IOException {
        byte[] content = loadBytesFromFile(file, Collections.emptyList());
        try (InputStream is = new ByteArrayInputStream(content);
             Reader reader = new InputStreamReader(is)) {
            return gson.fromJson(reader, resultClass);
        }
    }

    protected boolean contains(Path path, Path part) {
        List<String> tokens = new LinkedList<>();
        List<String> subTokens = new LinkedList<>();
        path.toAbsolutePath().iterator().forEachRemaining(p -> tokens.add(p.toFile().getName()));
        part.iterator().forEachRemaining(p -> subTokens.add(p.toFile().getName()));
        return Collections.lastIndexOfSubList(tokens, subTokens) >= 0;
    }

    private byte[] loadBytes(Layer layer, Object[] path) throws IOException {
        if (path == null || path.length == 0) {
            return null;
        }
        List<String> pathStr = Arrays.stream(path).map(Object::toString)
                .collect(Collectors.toCollection(LinkedList::new));
        if (!layer.getFile().getName().endsWith(".jar")) {
            pathStr.remove(0);
            if (pathStr.isEmpty()) {
                return null;
            }
            return loadBytesFromFile(layer.getFile().getParentFile(), pathStr);
        } else {
            return loadBytesFromZipEntry(layer.getFile(), pathStr);
        }
    }

    private byte[] loadBytesFromFile(File file, List<String> path) throws IOException {
        for (String part : path) {
            file = new File(file, part);
        }
        return Files.readAllBytes(file.toPath());
    }

    private byte[] loadBytesFromZipEntry(File file, List<String> path) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry zipEntry = zipFile.getEntry(String.join("/", path));
        try (InputStream is = zipFile.getInputStream(zipEntry);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[2048];

            while ((nRead = is.readNBytes(data, 0, data.length)) != 0) {
                os.write(data, 0, nRead);
            }

            os.flush();
            return os.toByteArray();
        }
    }
}
