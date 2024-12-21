package com.mcmiddleearth.rpmanager;

import com.google.gson.GsonBuilder;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.model.internal.Settings;
import com.mcmiddleearth.rpmanager.utils.BlockStateUtils;
import org.pushingpixels.radiance.theming.api.skin.*;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class ResourcePackManager {
    public static void main(String[] args) throws IOException {
        Settings settings = loadSettings();
        JFrame.setDefaultLookAndFeelDecorated(false);
        BlockStateUtils.init();
        SwingUtilities.invokeLater(() -> {
            Stream.of(
                    new RadianceAutumnLookAndFeel(),
                    new RadianceBusinessBlackSteelLookAndFeel(),
                    new RadianceBusinessBlueSteelLookAndFeel(),
                    new RadianceBusinessLookAndFeel(),
                    new RadianceCeruleanLookAndFeel(),
                    new RadianceCremeCoffeeLookAndFeel(),
                    new RadianceCremeLookAndFeel(),
                    new RadianceDustCoffeeLookAndFeel(),
                    new RadianceDustLookAndFeel(),
                    new RadianceGeminiLookAndFeel(),
                    new RadianceGraphiteAquaLookAndFeel(),
                    new RadianceGraphiteChalkLookAndFeel(),
                    new RadianceGraphiteElectricLookAndFeel(),
                    new RadianceGraphiteGlassLookAndFeel(),
                    new RadianceGraphiteGoldLookAndFeel(),
                    new RadianceGraphiteLookAndFeel(),
                    new RadianceGraphiteSiennaLookAndFeel(),
                    new RadianceGraphiteSunsetLookAndFeel(),
                    new RadianceGreenMagicLookAndFeel(),
                    new RadianceMagellanLookAndFeel(),
                    new RadianceMarinerLookAndFeel(),
                    new RadianceMistAquaLookAndFeel(),
                    new RadianceMistSilverLookAndFeel(),
                    new RadianceModerateLookAndFeel(),
                    new RadianceNebulaAmethystLookAndFeel(),
                    new RadianceNebulaBrickWallLookAndFeel(),
                    new RadianceNebulaLookAndFeel(),
                    new RadianceNightShadeLookAndFeel(),
                    new RadianceRavenLookAndFeel(),
                    new RadianceSaharaLookAndFeel(),
                    new RadianceSentinelLookAndFeel(),
                    new RadianceTwilightLookAndFeel())
                    .forEach(laf -> UIManager.installLookAndFeel(
                            new UIManager.LookAndFeelInfo(laf.getName(), laf.getClass().getCanonicalName())));
            new MainWindow(settings);
        });
    }

    private static Settings loadSettings() throws FileNotFoundException {
        Settings settings = new Settings();
        if (Settings.FILE.exists()) {
            settings = new GsonBuilder().create().fromJson(new FileReader(Settings.FILE), Settings.class);
        }
        return settings;
    }
}
