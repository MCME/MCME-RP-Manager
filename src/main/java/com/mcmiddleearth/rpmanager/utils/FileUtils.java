/*
 * Copyright (C) 2023 MCME
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FileUtils {
    private FileUtils() {}

    public static Object getFileRestoreData(File file) {
        if (!file.exists()) {
            return null;
        } else if (file.isDirectory()) {
            Map<String, Object> innerData = new LinkedHashMap<>();
            for (File innerFile : Objects.requireNonNull(file.listFiles())) {
                innerData.put(innerFile.getName(), getFileRestoreData(innerFile));
            }
            return innerData;
        } else {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                return inputStream.readAllBytes();
            } catch (IOException e) {
                //TODO error dialog
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void restoreFileData(File directory, Map<String, Object> restoreData) {
        restoreData.forEach((name, content) -> {
            File targetFile = new File(directory, name);
            if (targetFile.exists()) {
                boolean success = false;
                if (targetFile.isDirectory()) {
                    try {
                        org.apache.commons.io.FileUtils.deleteDirectory(targetFile);
                        success = true;
                    } catch (IOException e) {
                        //nop
                    }
                } else {
                    success = targetFile.delete();
                }
                if (!success) {
                    //TODO error dialog
                    throw new RuntimeException("Failed to remove file");
                }
            }
            if (content instanceof byte[] bytes) {
                try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    //TODO error dialog
                    throw new RuntimeException(e);
                }
            } else if (content != null) {
                Map<String, Object> innerData = (Map<String, Object>) content;
                if (targetFile.mkdir()) {
                    restoreFileData(targetFile, innerData);
                } else {
                    //TODO error dialog
                    throw new RuntimeException("Failed to create directory");
                }
            }
        });
    }
}
