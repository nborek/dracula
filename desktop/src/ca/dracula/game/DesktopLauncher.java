package ca.dracula.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ca.dracula.game.Drop;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Vampire Survivor");
		config.setWindowedMode(800, 480);
		config.useVsync(true);
		config.setForegroundFPS(60);
		new Lwjgl3Application(new Drop(), config);
	}
}
