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

package com.mcmiddleearth.rpmanager.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.model.*;
import com.mcmiddleearth.rpmanager.model.internal.LayerRelatedFiles;
import com.mcmiddleearth.rpmanager.model.internal.RelatedFiles;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.model.project.Layer;
import com.mcmiddleearth.rpmanager.model.project.Project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ResourcePackUtils {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().setLenient().enableComplexMapKeySerialization().create();
    private static final String MINECRAFT_PREFIX = "minecraft:";
    private static final String[] BLOCK_STATE_DIR_PATH = new String[] { "assets", "minecraft", "blockstates" };
    private static final String[] MODEL_DIR_PATH = new String[] { "assets", "minecraft", "models" };
    private static final String[] TEXTURES_DIR_PATH = new String[] { "assets", "minecraft", "textures" };
    private static final Pattern BLOCK_STATE_NAME_PATTERN =
            Pattern.compile("^(?:" + Pattern.quote(MINECRAFT_PREFIX) + ")?([^\\[#]+)(?:\\[[^]]+])?$");
    private static final Pattern BLOCK_STATE_NAME_PATTERN2 =
            Pattern.compile("^(?:" + Pattern.quote(MINECRAFT_PREFIX) + ")?([^#]+)#(?:.*)?$");

    private ResourcePackUtils() {}

    public static void saveFile(Object data, File target) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
            fileOutputStream.write(GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void compileProject(Project project, File target) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(target))) {}
        try (FileSystem fs = FileSystems.newFileSystem(target.toPath())) {
            for (int i = 1; i < project.getLayers().size(); ++i) {
                com.mcmiddleearth.rpmanager.model.project.Layer layer = project.getLayers().get(i);
                File layerDir = layer.getFile().getParentFile();
                recursiveCopy(layerDir, fs);
            }
        }
    }

    private static void recursiveCopy(File dir, FileSystem fileSystem) throws IOException {
        recursiveCopy(dir, dir.toPath(), fileSystem);
    }

    private static void recursiveCopy(File dir, Path basePath, FileSystem fs) throws IOException {
        for (File f : dir.listFiles()) {
            Path path = basePath.relativize(f.toPath());
            Path target = fs.getPath(path.toString());
            if (f.isDirectory()) {
                if (!".git".equals(f.getName())) { //skip .git directory, TODO: make this configurable
                    if (!Files.exists(target)) {
                        Files.createDirectory(target);
                    }
                    recursiveCopy(f, basePath, fs);
                }
            } else {
                Files.copy(f.toPath(), target, REPLACE_EXISTING);
            }
        }
    }

    public static RelatedFiles getRelatedFiles(BlockState blockState, Project project) throws IOException {
        List<LayerRelatedFiles> relatedModels = getRelatedModels(blockState, project);
        List<LayerRelatedFiles> relatedTextures = getRelatedTextures(relatedModels, project);
        return new RelatedFiles(relatedModels, relatedTextures);
    }

    public static RelatedFiles getRelatedFiles(BaseModel model, Project project) throws IOException {
        List<String> models = model.getParent() == null ?
                Collections.emptyList() :
                Stream.of(model.getParent()).map(ResourcePackUtils::removePrefix)
                        .map(s -> s.contains("/") ? s : model instanceof BlockModel ? "block/" + s : "item/" + s)
                        .toList();
        List<LayerRelatedFiles> relatedModels = getModels(models, project);
        List<LayerRelatedFiles> relatedTextures = getRelatedTextures(
                Stream.concat(
                        Stream.of(new LayerRelatedFiles(null,
                                Collections.singletonList(new SelectedFileData(model, "", new Object[0])))),
                        relatedModels.stream())
                        .toList(),
                project);
        return new RelatedFiles(relatedModels, relatedTextures);
    }

    public static List<LayerRelatedFiles> getMatchingBlockStates(Project project, String blockState)
            throws IOException {
        List<LayerRelatedFiles> result = new LinkedList<>();
        Matcher matcher = BLOCK_STATE_NAME_PATTERN.matcher(blockState);
        Matcher matcher2 = BLOCK_STATE_NAME_PATTERN2.matcher(blockState);
        Matcher matching = null;
        if (matcher.matches()) {
            matching = matcher;
        } else if (matcher2.matches()) {
            matching = matcher2;
        }
        if (matching != null) {
            String name = matching.group(1);
            Object[] path = Stream.concat(Stream.of(BLOCK_STATE_DIR_PATH), Stream.of((name + ".json").split("/")))
                    .toArray();
            for (Layer layer : project.getLayers()) {
                if (containsFile(layer, path)) {
                    SelectedFileData selectedFileData = FileLoader.load(layer, Stream.concat(
                            layer.getFile().getName().endsWith(".jar") ? Stream.empty() : Stream.of(layer.getName()),
                            Stream.of(path)).toArray());
                    result.add(new LayerRelatedFiles(layer.getName(), Collections.singletonList(selectedFileData)));
                }
            }
        }
        return result;
    }

    private static List<LayerRelatedFiles> getRelatedModels(BlockState blockState, Project project) throws IOException {
        List<String> models = Optional.ofNullable(blockState.getVariants())
                .map(v -> v.values().stream().flatMap(l -> l.stream().map(Model::getModel)))
                .orElseGet(() -> blockState.getMultipart().stream().flatMap(
                        c -> c.getApply().stream().map(Model::getModel)))
                .distinct()
                .map(ResourcePackUtils::removePrefix)
                .map(s -> s.contains("/") ? s : "block/" + s)
                .toList();
        return getModels(models, project);
    }

    private static List<LayerRelatedFiles> getModels(List<String> models, Project project) throws IOException {
        List<LayerRelatedFiles> result = new LinkedList<>();
        for (com.mcmiddleearth.rpmanager.model.project.Layer layer : project.getLayers()) {
            List<SelectedFileData> layerModels = new LinkedList<>();
            for (String model : models) {
                Object[] path = Stream.concat(Stream.of(MODEL_DIR_PATH), Stream.of((model + ".json").split("/")))
                        .toArray();
                if (containsFile(layer, path)) {
                    layerModels.add(FileLoader.load(layer, Stream.concat(
                            layer.getFile().getName().endsWith(".jar") ? Stream.empty() : Stream.of(layer.getName()),
                            Stream.of(path)).toArray()));
                }
            }
            if (!layerModels.isEmpty()) {
                result.add(new LayerRelatedFiles(layer.getName(), layerModels));
            }
        }
        return result;
    }

    private static List<LayerRelatedFiles> getRelatedTextures(List<LayerRelatedFiles> models, Project project)
            throws IOException {
        List<String> textures = models.stream()
                .flatMap(files -> files.getRelatedFiles().stream())
                .flatMap(fileData -> Optional.ofNullable(((BaseModel) fileData.getData()).getTextures()).stream()
                        .flatMap(t -> t.values().stream()))
                .map(ResourcePackUtils::removePrefix)
                .distinct()
                .toList();
        List<LayerRelatedFiles> result = new LinkedList<>();
        for (com.mcmiddleearth.rpmanager.model.project.Layer layer : project.getLayers()) {
            List<SelectedFileData> layerTextures = new LinkedList<>();
            for (String texture : textures) {
                Object[] path = Stream.concat(Stream.of(TEXTURES_DIR_PATH), Stream.of((texture + ".png").split("/")))
                        .toArray();
                if (containsFile(layer, path)) {
                    layerTextures.add(FileLoader.load(layer, Stream.concat(
                            layer.getFile().getName().endsWith(".jar") ? Stream.empty() : Stream.of(layer.getName()),
                            Stream.of(path)).toArray()));
                }
            }
            if (!layerTextures.isEmpty()) {
                result.add(new LayerRelatedFiles(layer.getName(), layerTextures));
            }
        }
        return result;
    }

    private static String removePrefix(String path) {
        return path.replace(MINECRAFT_PREFIX, "");
    }

    private static boolean containsFile(com.mcmiddleearth.rpmanager.model.project.Layer layer, Object[] path)
            throws IOException {
        if (path == null || path.length == 0) {
            return false;
        }
        List<String> pathStr = Arrays.stream(path).map(Object::toString)
                .collect(Collectors.toCollection(LinkedList::new));
        if (!layer.getFile().getName().endsWith(".jar")) {
            if (pathStr.isEmpty()) {
                return false;
            }
            return containsFile(layer.getFile().getParentFile(), pathStr);
        } else {
            return zipContainsFile(layer.getFile(), pathStr);
        }
    }

    private static boolean containsFile(File file, List<String> path) {
        for (String part : path) {
            file = new File(file, part);
        }
        return file.exists();
    }

    private static boolean zipContainsFile(File file, List<String> path) throws IOException {
        try (ZipFile zipFile = new ZipFile(file)) {
            return zipFile.getEntry(String.join("/", path)) != null;
        }
    }

    private static <T> List<T> reverse(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
}
