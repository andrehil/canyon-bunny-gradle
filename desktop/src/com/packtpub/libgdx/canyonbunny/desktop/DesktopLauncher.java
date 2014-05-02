package com.packtpub.libgdx.canyonbunny.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.packtpub.libgdx.canyonbunny.CanyonBunnyMain;

import java.io.File;

public class DesktopLauncher {

	private static boolean rebuildAtlas = true;
	private static boolean drawDebugLines = false;

	public static void main(String[] args) {
		System.setProperty("user.name", "EnglishWords");
		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugLines;
			settings.duplicatePadding = true;

			deleteFile("./images/canyonbunny.pack");
			deleteFile("./images/canyonbunny-ui.pack");

			TexturePacker.process(settings, "../../desktop/assets-raw/images", "./images", "canyonbunny.pack");
			TexturePacker.process(settings, "../../desktop/assets-raw/images-ui", "./images", "canyonbunny-ui.pack");
		}

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CanyonBunny";
		cfg.width = 200;
		cfg.height = 120;

		new LwjglApplication(new CanyonBunnyMain(), cfg);
	}

	private static void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

}
