package com.cs638.game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Cannon";
		cfg.useGL20 = true;
		cfg.width = Constants.WORLD_WIDTH;
		cfg.height = Constants.WORLD_HEIGHT;
		
		new LwjglApplication(new CannonShuffle(), cfg);
	}
}