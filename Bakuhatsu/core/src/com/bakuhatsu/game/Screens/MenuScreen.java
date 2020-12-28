package com.bakuhatsu.game.Screens;

import com.bakuhatsu.game.Controller;
import com.bakuhatsu.game.Main;
import com.bakuhatsu.game.utils.Projectile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class MenuScreen implements Screen {
    private final Stage stage;
    private final Stage backStage;
    private final Projectile projectile;
    private final float radius;
    private float elapsedTime;
    private final Vector2 position;
    //private OrthographicCamera camera;

    public MenuScreen(final Controller controller) {
        stage = new Stage(new StretchViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        backStage = new Stage(new StretchViewport(Main.GAME_WIDTH, Main.GAME_HEIGHT));

        Gdx.app.log("scree", Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

        projectile = new Projectile(new World(new Vector2(0, -9.8f), true), Main.GAME_WIDTH / 2f, Main.GAME_HEIGHT / 2f);
        position = new Vector2(Main.GAME_WIDTH / 2f, Main.GAME_HEIGHT / 2f);
        radius = Main.GAME_WIDTH / 3f;
        projectile.setScale(2f);
        projectile.constructBody(0, 0, 0,0, 2f);
        backStage.addActor(projectile);
        Gdx.input.setInputProcessor(stage);
        //camera = (OrthographicCamera) stage.getViewport().getCamera();

        float heightFactor = Main.GAME_HEIGHT / 12f;
        float widthFactor = Main.GAME_WIDTH / 12f;

        Label title = new Label("Bakuhatsu", Main.SKIN, "default");
        title.setSize(Main.GAME_WIDTH, heightFactor);
        title.setPosition(0, 7 * heightFactor);
        title.setAlignment(Align.center);
        title.setColor(Color.WHITE);
        title.setFontScale(3);
        stage.addActor(title);

        TextButton playButton = new TextButton("Play", Main.SKIN, "default");
        playButton.setSize(4 * widthFactor, heightFactor);
        playButton.setPosition(Main.GAME_WIDTH / 2f - 2 * widthFactor, 5 * heightFactor);
        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.changeToGameScreen();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        stage.addActor(playButton);

        TextButton scoreButton = new TextButton("Scoreboard", Main.SKIN, "default");
        scoreButton.setSize(4 * widthFactor, heightFactor);
        scoreButton.setPosition(Main.GAME_WIDTH / 2f - 2 * widthFactor, 3 * heightFactor);
        scoreButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.changeToScoreScreen(false);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        stage.addActor(scoreButton);

        TextButton exitButton = new TextButton("Exit", Main.SKIN, "default");
        exitButton.setSize(4 * widthFactor, heightFactor);
        exitButton.setPosition(Main.GAME_WIDTH / 2f - 2 * widthFactor, heightFactor);
        exitButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.exitCall();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        stage.addActor(exitButton);

    }

    @Override
    public void show() {
        Gdx.app.log("MainScreen","Show");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        elapsedTime += delta * 1.5f;
        if(elapsedTime > 2 * Math.PI)
            elapsedTime = 0;

        float x = (float) Math.cos(elapsedTime) * radius;
        float y = (float) Math.sin(elapsedTime) * radius;
        projectile.setBodyMovement( (float)(-y), (float)(x), position.x + x, position.y + y, elapsedTime);

        backStage.act();
        stage.act();

        backStage.draw();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
