package no.sandramoen.ggj2021oslo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.ggj2021oslo.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Where's My Video Call?";
		config.resizable = false;
		config.width = 1400;
		config.height = 500;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
