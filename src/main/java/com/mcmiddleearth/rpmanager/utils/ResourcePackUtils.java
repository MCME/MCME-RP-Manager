package com.mcmiddleearth.rpmanager.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.BlockState;
import com.mcmiddleearth.rpmanager.model.ItemModel;
import com.mcmiddleearth.rpmanager.model.internal.Layer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackUtils {
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String BLOCK_STATE_DIR = "assets/minecraft/blockstates";
    private static final String BLOCK_MODEL_DIR = "assets/minecraft/models/block";
    private static final String MODEL_DIR = "assets/minecraft/models";
    private static final String TEXTURES_DIR = "assets/minecraft/textures";

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
}
