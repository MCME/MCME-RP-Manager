package com.mcmiddleearth.rpmanager;

import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.utils.BlockStateUtils;

import java.io.IOException;

public class ResourcePackManager {
    public static void main(String[] args) throws IOException {
        BlockStateUtils.init();
        new MainWindow();
    }
}
