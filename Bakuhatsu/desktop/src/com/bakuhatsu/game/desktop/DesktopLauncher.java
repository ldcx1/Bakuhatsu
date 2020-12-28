package com.bakuhatsu.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bakuhatsu.game.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		//config.setWindowPosition(1920, 0);
		config.setWindowSizeLimits(1600,900, 1920, 1080);
		config.setTitle("Bakuhatsu");
		config.setResizable(true);
		new Lwjgl3Application(new Main(), config);
	}
}
