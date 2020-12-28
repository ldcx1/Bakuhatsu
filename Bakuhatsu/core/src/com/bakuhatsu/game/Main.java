package com.bakuhatsu.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Main extends Game {
	public static Skin SKIN;
	public static final float WINDOW_WIDTH = 1600;
	public static final float WINDOW_HEIGHT = 900;
	public static final float GAME_WIDTH = 640;
	public static final float GAME_HEIGHT = 480;
	public static final String scoreFile = "score.txt";

 	@Override
	public void create() {
		Gdx.app.log("Main", "Start");
		SKIN = new Skin(Gdx.files.internal("skin/clean-crispy-ui.json"));
		View view = new View(this);
		Model model = new Model();
		Controller controller = new Controller(view, model);
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
	}

	@Override
	public void dispose () {
		SKIN.dispose();
	}
}
