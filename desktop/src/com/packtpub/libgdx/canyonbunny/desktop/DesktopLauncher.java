package com.packtpub.libgdx.canyonbunny.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.packtpub.libgdx.canyonbunny.CanyonBunnyMain;

public class DesktopLauncher {

    private static boolean rebuildAtlas = false;
    private static boolean drawDebugLines = false;

    public static void main(String[] args) {
        System.setProperty("user.name", "EnglishWords");
        if (rebuildAtlas) {
            Settings settings = new Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.debug = drawDebugLines;
            settings.duplicatePadding = true;

            TexturePacker.process(settings, "../../desktop/assets-raw/images", "./images", "canyonbunny.pack");
            TexturePacker.process(settings, "../../desktop/assets-raw/images-ui", "./images", "canyonbunny-ui.pack");
        }

        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "CanyonBunny";
        cfg.width = 800;
        cfg.height = 480;

        new LwjglApplication(new CanyonBunnyMain(), cfg);
    }

}
