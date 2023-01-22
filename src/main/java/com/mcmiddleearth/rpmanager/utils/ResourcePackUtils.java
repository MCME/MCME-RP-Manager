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
import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.ItemModel;
import com.mcmiddleearth.rpmanager.model.Model;
import com.mcmiddleearth.rpmanager.model.internal.Layer;
import com.mcmiddleearth.rpmanager.model.wrappers.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackUtils {
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().setLenient().enableComplexMapKeySerialization().create();
    private static final String BLOCK_STATE_DIR = "assets/minecraft/blockstates";
    private static final String BLOCK_MODEL_DIR = "assets/minecraft/models/block";
    private static final String MODEL_DIR = "assets/minecraft/models";
    private static final String TEXTURES_DIR = "assets/minecraft/textures";
    private static final String MINECRAFT_PREFIX = "minecraft:";

    private static final Function<InputStream, BlockState, IOException> BLOCK_STATE_READER =
            gsonReader(BlockState.class);
    private static final Function<InputStream, BlockModel, IOException> BLOCK_MODEL_READER =
            gsonReader(BlockModel.class);
    private static final Function<InputStream, ItemModel, IOException> ITEM_MODEL_READER =
            gsonReader(ItemModel.class);

    private ResourcePackUtils() {}

    public static Layer loadLayer(File file) throws IOException {
        if (file.isDirectory()) {
            return loadLayerFromDirectory(file);
        } else if ("pack.mcmeta".equals(file.getName())) {
            return loadLayerFromDirectory(file.getParentFile());
        } else if (file.getName().endsWith(".zip") || file.getName().endsWith(".jar")) {
            return loadLayerFromZipFile(file);
        } else {
            throw new IllegalArgumentException("Invalid resource pack path");
        }
    }

    public static ResourcePackData getStructure(Layer current, Layer urps, Layer vanilla) {
        Set<String> texturePaths = new TreeSet<>(current.getTextures().keySet());
        texturePaths.addAll(urps.getTextures().keySet());
        texturePaths.addAll(vanilla.getTextures().keySet());
        Map<String, TextureWrapper> textureWrappers = texturePaths.stream().collect(Collectors.toMap(
                java.util.function.Function.identity(),
                s -> new TextureWrapper(s, current.getTextures().get(s), urps.getTextures().get(s),
                        vanilla.getTextures().get(s)), throwingMerger(), TreeMap::new));

        Set<String> blockModelPaths = new TreeSet<>(current.getBlockModels().keySet());
        blockModelPaths.addAll(urps.getBlockModels().keySet());
        blockModelPaths.addAll(vanilla.getBlockModels().keySet());
        Map<String, BlockModelWrapper> blockModelWrappers = blockModelPaths.stream().collect(Collectors.toMap(
                java.util.function.Function.identity(),
                s -> wrapBlockModel(current, urps, vanilla, s, textureWrappers), throwingMerger(), TreeMap::new));

        Set<String> itemModelPaths = new TreeSet<>(current.getItemModels().keySet());
        itemModelPaths.addAll(urps.getItemModels().keySet());
        itemModelPaths.addAll(vanilla.getItemModels().keySet());
        Map<String, ItemModelWrapper> itemModelWrappers = itemModelPaths.stream().collect(Collectors.toMap(
                java.util.function.Function.identity(),
                s -> wrapItemModel(current, urps, vanilla, s, textureWrappers), throwingMerger(), TreeMap::new));
        fillParents(Stream.concat(blockModelWrappers.entrySet().stream(), itemModelWrappers.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        Set<String> blockStatePaths = new TreeSet<>(current.getBlockStates().keySet());
        blockStatePaths.addAll(urps.getBlockStates().keySet());
        blockStatePaths.addAll(vanilla.getBlockStates().keySet());
        List<BlockStateWrapper> blockStateWrappers = blockStatePaths.stream()
                .map(s -> wrapBlockState(current, urps, vanilla, s, blockModelWrappers)).collect(Collectors.toList());

        ResourcePackData resourcePackData = new ResourcePackData();
        resourcePackData.setBlockStates(blockStateWrappers);
        resourcePackData.setItemModels(new ArrayList<>(itemModelWrappers.values()));
        return resourcePackData;
    }

    public static void saveFile(Object data, File target) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
            fileOutputStream.write(GSON.toJson(data).getBytes(StandardCharsets.UTF_8));
        }
    }

    private static BlockModelWrapper wrapBlockModel(Layer current, Layer urps, Layer vanilla, String filePath,
                                                    Map<String, TextureWrapper> textureWrappers) {
        return new BlockModelWrapper(filePath,
                wrapBlockModel(current, filePath, textureWrappers),
                wrapBlockModel(urps, filePath, textureWrappers),
                wrapBlockModel(vanilla, filePath, textureWrappers));
    }

    private static ItemModelWrapper wrapItemModel(Layer current, Layer urps, Layer vanilla, String filePath,
                                                  Map<String, TextureWrapper> textureWrappers) {
        return new ItemModelWrapper(filePath,
                wrapItemModel(current, filePath, textureWrappers),
                wrapItemModel(urps, filePath, textureWrappers),
                wrapItemModel(vanilla, filePath, textureWrappers));
    }

    private static BlockStateWrapper wrapBlockState(Layer current, Layer urps, Layer vanilla, String filePath,
                                                    Map<String, BlockModelWrapper> blockModelWrappers) {
        return new BlockStateWrapper(filePath,
                wrapBlockState(current, filePath, blockModelWrappers),
                wrapBlockState(urps, filePath, blockModelWrappers),
                wrapBlockState(vanilla, filePath, blockModelWrappers));
    }

    private static BlockModelData wrapBlockModel(Layer layer, String filePath,
                                                 Map<String, TextureWrapper> textureWrappers) {
        BlockModel blockModel = layer.getBlockModels().get(filePath);
        if (blockModel == null) {
            return null;
        }
        BlockModelData blockModelData = new BlockModelData();
        blockModelData.setModel(blockModel);
        blockModelData.setTextures(Optional.ofNullable(blockModel.getTextures()).orElse(Collections.emptyMap())
                .values().stream()
                .filter(s -> !s.startsWith("#"))
                .distinct()
                .map(s -> textureWrappers.get(TEXTURES_DIR + "/" + removePrefix(s) + ".png"))
                .collect(Collectors.toList()));
        return blockModelData;
    }

    private static ItemModelData wrapItemModel(Layer layer, String filePath,
                                               Map<String, TextureWrapper> textureWrappers) {
        ItemModel itemModel = layer.getItemModels().get(filePath);
        if (itemModel == null) {
            return null;
        }
        ItemModelData itemModelData = new ItemModelData();
        itemModelData.setModel(itemModel);
        itemModelData.setTextures(Optional.ofNullable(itemModel.getTextures()).orElse(Collections.emptyMap())
                .values().stream()
                .filter(s -> !s.startsWith("#"))
                .distinct()
                .map(s -> textureWrappers.get(TEXTURES_DIR + "/" + removePrefix(s) + ".png"))
                .collect(Collectors.toList()));
        return itemModelData;
    }

    private static BlockStateData wrapBlockState(Layer layer, String filePath,
                                                 Map<String, BlockModelWrapper> blockModelWrappers) {
        BlockState blockState = layer.getBlockStates().get(filePath);
        if (blockState == null) {
            return null;
        }
        BlockStateData blockStateData = new BlockStateData();
        blockStateData.setBlockState(blockState);
        blockStateData.setBlockModels(
                Optional.ofNullable(blockState.getVariants())
                        .map(v -> v.values().stream().flatMap(l -> l.stream().map(Model::getModel)))
                        .orElseGet(() -> blockState.getMultipart().stream().flatMap(
                                c -> c.getApply().stream().map(Model::getModel)))
                        .distinct()
                        .map(s -> blockModelWrappers.get(MODEL_DIR + "/" + removePrefix(s) + ".json"))
                        .collect(Collectors.toList()));
        return blockStateData;
    }

    private static void fillParents(Map<String, ModelWrapper<?>> models) {
        for (ModelWrapper<?> modelWrapper : models.values()) {
            fillParent(modelWrapper.getCurrent(), models);
            fillParent(modelWrapper.getUrps(), models);
            fillParent(modelWrapper.getVanilla(), models);
        }
    }

    private static void fillParent(ModelData<?> modelData, Map<String, ModelWrapper<?>> models) {
        if (modelData != null && modelData.getModel().getParent() != null) {
            modelData.setParent(models.get(MODEL_DIR + "/" + removePrefix(modelData.getModel().getParent()) + ".json"));
        }
    }

    private static String removePrefix(String path) {
        return path.replace(MINECRAFT_PREFIX, "");
    }

    private static Layer loadLayerFromDirectory(File directory) throws IOException {
        Layer layer = new Layer();
        layer.setBlockStates(loadBlockStates(directory));
        layer.setBlockModels(loadBlockModels(directory));
        layer.setItemModels(loadItemModels(directory));
        layer.setTextures(loadTextures(directory));
        return layer;
    }

    private static Layer loadLayerFromZipFile(File zipFile) throws IOException {
        Layer layer = new Layer();
        layer.setBlockStates(loadBlockStatesFromZipFile(zipFile));
        layer.setBlockModels(loadBlockModelsFromZipFile(zipFile));
        layer.setItemModels(loadItemModelsFromZipFile(zipFile));
        layer.setTextures(loadTexturesFromZipFile(zipFile));
        return layer;
    }

    private static Map<String, BlockState> loadBlockStates(File packDirectory) throws IOException {
        return loadItems(packDirectory, new File(packDirectory, BLOCK_STATE_DIR), BLOCK_STATE_READER);
    }

    private static Map<String, BlockState> loadBlockStatesFromZipFile(File zipFile) throws IOException {
        return loadItemsFromZipFile(zipFile, BLOCK_STATE_DIR, BLOCK_STATE_READER);
    }

    private static Map<String, BlockModel> loadBlockModels(File packDirectory) throws IOException {
        return loadItems(packDirectory, new File(packDirectory, BLOCK_MODEL_DIR), BLOCK_MODEL_READER);
    }

    private static Map<String, BlockModel> loadBlockModelsFromZipFile(File zipFile) throws IOException {
        return loadItemsFromZipFile(zipFile, BLOCK_MODEL_DIR, BLOCK_MODEL_READER);
    }

    private static Map<String, ItemModel> loadItemModels(File packDirectory) throws IOException {
        return loadItems(packDirectory, new File(packDirectory, MODEL_DIR), ITEM_MODEL_READER, BLOCK_MODEL_DIR);
    }

    private static Map<String, ItemModel> loadItemModelsFromZipFile(File zipFile) throws IOException {
        return loadItemsFromZipFile(zipFile, MODEL_DIR, ITEM_MODEL_READER, BLOCK_MODEL_DIR);
    }

    private static Map<String, BufferedImage> loadTextures(File packDirectory) throws IOException {
        return loadItems(packDirectory, new File(packDirectory, TEXTURES_DIR), ImageIO::read);
    }

    private static Map<String, BufferedImage> loadTexturesFromZipFile(File zipFile) throws IOException {
        return loadItemsFromZipFile(zipFile, TEXTURES_DIR, ImageIO::read);
    }

    private static <T> Map<String, T> loadItems(File packDirectory, File directory,
                                                Function<InputStream, T, IOException> reader,
                                                String... excludedDirectories)
            throws IOException {
        Map<String, T> result = new LinkedHashMap<>();
        for (File f : Objects.requireNonNull(directory.listFiles())) {
            String filePath = getFilePath(packDirectory, f);
            if (f.isDirectory()) {
                if (Arrays.stream(excludedDirectories)
                        .noneMatch(excludedDirectory -> excludedDirectory.equals(filePath))) {
                    result.putAll(loadItems(packDirectory, f, reader, excludedDirectories));
                }
            } else if (!filePath.endsWith(".mcmeta")) {
                try (FileInputStream fileInputStream = new FileInputStream(f)) {
                    result.put(filePath, reader.apply(fileInputStream));
                }
            }
        }
        return result;
    }

    private static <T> Map<String, T> loadItemsFromZipFile(File zipFile, String directory,
                                                           Function<InputStream, T, IOException> reader,
                                                           String... excludeDirectories) throws IOException {
        ZipFile file = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = file.entries();
        Map<String, T> result = new LinkedHashMap<>();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String filePath = zipEntry.getName();
            if (!zipEntry.isDirectory() && filePath.startsWith(directory + "/") && !filePath.endsWith(".mcmeta") &&
                    Arrays.stream(excludeDirectories)
                            .noneMatch(excludedDirectory -> filePath.startsWith(excludedDirectory + "/"))) {
                try (InputStream inputStream = file.getInputStream(zipEntry)) {
                    result.put(filePath, reader.apply(inputStream));
                }
            }
        }
        return result;
    }

    private static <T> Function<InputStream, T, IOException> gsonReader(Class<T> targetClass) {
        return inputStream -> {
            try (InputStreamReader reader = new InputStreamReader(inputStream)) {
                return GSON.fromJson(removeTrailingCommas(reader), targetClass);
            }
        };
    }

    private static String getFilePath(File baseDirectory, File file) {
        return baseDirectory.toPath().relativize(file.toPath()).toString();
    }

    private static String removeTrailingCommas(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        reader.transferTo(writer);
        return writer.toString()
                .replaceAll(",(\\s+})", "$1")
                .replaceAll(",(\\s+])", "$1");
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
