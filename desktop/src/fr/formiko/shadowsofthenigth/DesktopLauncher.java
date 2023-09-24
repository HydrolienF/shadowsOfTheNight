package fr.formiko.shadowsofthenigth;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(-1);
		config.useVsync(true);
		config.setTitle("Shadows of the night");
		config.setMaximized(true);
		// config.setWindowIcon("images/icon.png");
		new Lwjgl3Application(new ShadowsOfTheNight(), config);
	}
}
