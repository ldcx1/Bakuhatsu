package com.bakuhatsu.game.Screens;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.bakuhatsu.game.Controller;
import com.bakuhatsu.game.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bakuhatsu.game.utils.*;
import java.util.Random;

public class GameScreen implements Screen {
    private static final float RAMBLING_POWER = 0.75f;
    private static final int INITIAL_TARGET_COUNT = 3;

    private final Random random;
    private final Stage gameStage;
    private final Stage UIStage;
    private final OrthographicCamera camera;
    private final InputMultiplexer multiplexer;
    private final Box2DDebugRenderer debugRenderer;
    private final World world;
    private final Label fpsCounter;

    private final LifeBar lifeBar;
    private final Score score;
    private final Player player;
    private final Controller controller;

    private final ParallaxBackground parallaxBackgroundSky;
    private final ParallaxBackground parallaxBackground;

    private boolean showCollider = false;
    private boolean attackCharging = false;
    private boolean die = false;

    private int targetCount = 0;
    private int maxTargetCount = INITIAL_TARGET_COUNT;

    private float attackElapsedTime;
    private float targetCountElapsedTime;

    private float angleProjectile, velocityXProjectile, velocityYProjectile;
    private int damageProjectile;
    private Vector3 fallBackAttackPoint = new Vector3(0, 0, 0);

    public static final float WALL_WIDTH;
    public static final float WALL_LENGTH;
    public static final float WALL_HEIGHT;
    public static final Vector2 WALL_POSITION_RIGHT;
    public static final Vector2 WALL_POSITION_LEFT;
    public static final Vector2 WALL_POSITION_UP;
    public static final Vector2 WALL_POSITION_DOWN;

    static {
        WALL_WIDTH = 10;
        WALL_HEIGHT = Main.GAME_HEIGHT - 85 + 250;
        WALL_LENGTH = Main.GAME_WIDTH + 150 + 300;

        WALL_POSITION_LEFT = new Vector2(-160, 85);
        WALL_POSITION_RIGHT = new Vector2(Main.GAME_WIDTH + 300, 85);
        WALL_POSITION_UP = new Vector2(-150, Main.GAME_HEIGHT + 250);
        WALL_POSITION_DOWN = new Vector2(-150, 85);
    }

    public GameScreen(final Controller controller) {
        this.controller = controller;
        random = new RandomXS128();

        gameStage = new Stage(new StretchViewport(Main.GAME_WIDTH, Main.GAME_HEIGHT));
        UIStage = new Stage(new StretchViewport(Main.GAME_WIDTH, Main.GAME_HEIGHT));

        fpsCounter = new Label("", Main.SKIN);
        fpsCounter.setPosition(Main.GAME_WIDTH - 90, Main.GAME_HEIGHT - 20);
        fpsCounter.setSize(80, 20);
        fpsCounter.setAlignment(Align.right);
        UIStage.addActor(fpsCounter);

        Texture backTexture = new Texture("background.png");
        Array<Texture> backTextures = new Array<Texture>();
        backTextures.add(backTexture);

        Texture skyTexture = new Texture("clouds.png");
        Array<Texture> skyTextures = new Array<Texture>();
        skyTextures.add(skyTexture);

        parallaxBackground = new ParallaxBackground(backTextures);
        parallaxBackground.setSize(Main.GAME_WIDTH, Main.GAME_HEIGHT);
        parallaxBackground.setOffset(0, 0, Main.GAME_WIDTH, Main.GAME_HEIGHT);
        gameStage.addActor(parallaxBackground);

        parallaxBackgroundSky = new ParallaxBackground(skyTextures);
        parallaxBackgroundSky.setSize(Main.GAME_WIDTH, skyTexture.getHeight() * (Main.GAME_HEIGHT / backTexture.getHeight()));
        parallaxBackgroundSky.setPosition(0, Main.GAME_HEIGHT - parallaxBackgroundSky.getHeight());
        parallaxBackgroundSky.setOffset(0, parallaxBackgroundSky.getY(), Main.GAME_WIDTH, parallaxBackgroundSky.getHeight());
        parallaxBackgroundSky.setOffsetSrc(0, - parallaxBackgroundSky.getY());
        parallaxBackgroundSky.setSpeedX(0.01f);
        gameStage.addActor(parallaxBackgroundSky);

        this.resize((int)Main.GAME_WIDTH, (int)Main.GAME_HEIGHT);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(UIStage);
        multiplexer.addProcessor(gameStage);

        lifeBar = new LifeBar();
        UIStage.addActor(lifeBar);
        score =  new Score();
        UIStage.addActor(score);

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(controller.getModel());

        gameStage.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(button == Input.Buttons.LEFT  && !attackCharging && !controller.pendingToInvokeProjectile() && !die) {
                    attackCharging = true;
                    attackElapsedTime = 0;
                    controller.beginAttack(unprotectCoordinates(x, y));
                    return true;
                }
                else if(button == Input.Buttons.RIGHT) {
                    showCollider = !showCollider;
                    return true;
                }
                else return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(attackCharging) {
                    fallBackAttackPoint = unprotectCoordinates(x, y);
                    controller.dragInput(fallBackAttackPoint);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(button == Input.Buttons.LEFT && attackCharging) {
                    attackCharging = false;
                    if(!controller.sendAttack(unprotectCoordinates(x, y), attackElapsedTime))
                        player.sadFinish();
                }
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                camera.zoom += amountY * 0.5f;
                Gdx.app.log("Camera", camera.viewportWidth *  camera.zoom + " " + camera.viewportHeight *  camera.zoom);
                float width = camera.viewportWidth *  camera.zoom;
                float height = camera.viewportWidth *  camera.zoom;
                parallaxBackground.setOffset((Main.GAME_WIDTH / 2 - width / 2), 0, width, parallaxBackground.getHeight());
                parallaxBackgroundSky.setOffset((Main.GAME_WIDTH / 2 - width / 2), parallaxBackgroundSky.getY(), width, height);
                camera.zoom -= .05f;
                return super.scrolled(event, x, y, amountX, amountY);
            }
        });

        player = new Player(Main.GAME_WIDTH / 50f, Main.GAME_HEIGHT / 5f);
        player.setParent(gameStage);

        camera = (OrthographicCamera) gameStage.getViewport().getCamera();

        new Wall(world, WALL_POSITION_LEFT, WALL_WIDTH, WALL_HEIGHT, WallType.Score);
        new Wall(world, WALL_POSITION_DOWN, WALL_LENGTH, WALL_WIDTH, WallType.Platform);
        new Wall(world, WALL_POSITION_UP, WALL_LENGTH, WALL_WIDTH, WallType.Destructive);
        new Wall(world, WALL_POSITION_RIGHT, WALL_WIDTH, WALL_HEIGHT, WallType.Destructive);

        camera.zoom = 0.95f;
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        Gdx.app.log("Game", "Begin");
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fpsCounter.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));

        if(die)  player.die(delta);
        world.step(delta, 6, 2);

        if(world.isLocked())
            Gdx.app.log("World locked", world.isLocked() + "");
        else
        {
            if(controller.pendingToInvokeTarget())
                controller.invokeTarget(delta);
            else if(targetCount < maxTargetCount)
                controller.invokeTarget(delta);

            if(controller.pendingToInvokeProjectile()) {
                player.finishAttack(angleProjectile, velocityXProjectile, velocityYProjectile, damageProjectile, attackElapsedTime);
                controller.setFlagProjectile();
            }
        }

        if(attackCharging) {
            attackElapsedTime += delta;
            if (!world.isLocked() && (attackElapsedTime > Player.MAX_CHARGE_TIME || die)) {
                finishAttack();
            }
            player.constructAttack(attackElapsedTime);
            this.rambleCamera(attackElapsedTime);
        }
        else {
            if(attackElapsedTime >= 0) {
                attackElapsedTime -= delta;
                this.rambleCamera(attackElapsedTime);
            }
        }

        targetCountElapsedTime += delta;
        if(targetCountElapsedTime > 10f) {
            maxTargetCount++;
            targetCountElapsedTime = 0;
        }

        UIStage.act();
        gameStage.act();
        gameStage.draw();
        UIStage.draw();

        if(showCollider)
            debugRenderer.render(world, camera.combined);
    }

    private void rambleCamera(float currentPower) {
        camera.position.x = Main.GAME_WIDTH / 2 + (random.nextFloat() - 0.5f) * RAMBLING_POWER * currentPower;
        camera.position.y = Main.GAME_HEIGHT / 2 + (random.nextFloat() - 0.5f) * RAMBLING_POWER * currentPower;
    }

    public void resetCameraPosition() {
        camera.position.x = Main.GAME_WIDTH / 2;
        camera.position.y = Main.GAME_HEIGHT / 2;
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);
        UIStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        UIStage.dispose();
        gameStage.dispose();
    }

    public void setArmRotation(float rotation) {
        player.setRotation(rotation);
    }

    public void launchProjectile(float angle, float velocityX, float velocityY, int damage) {
        angleProjectile = angle;
        velocityXProjectile = velocityX;
        velocityYProjectile = velocityY;
        damageProjectile = damage;
    }

    public void createTarget(TargetType type, float x, float y, float angle, float velocityX, float velocityY, int score, int life) {
            targetCount++;
            Gdx.app.log("Target", x + " " + y);
            switch (type) {
                case Angel: gameStage.addActor(new AngelTarget(world, x, y, angle, velocityX, velocityY, score, life)); break;
                case Ghoul: gameStage.addActor(new GhoulTarget(world, x, y, angle, velocityX, velocityY, score, life)); break;
                default: Gdx.app.error("Target", "Invalid target");
            }
    }

    public void addScore(int score) {
        this.score.increaseScore(score);
    }

    public void enemyDied() {
        Gdx.app.log("Target", "Target died");
        targetCount --;
    }

    public void beginAttack() {
        Projectile projectile = new Projectile(world, player.getLauncherPosition());
        gameStage.addActor(projectile);
        player.prepareAttack(projectile);
    }

    private Vector3 unprotectCoordinates(float x, float y) {
        Vector3 touchPos = new Vector3(x, y,0);
        camera.unproject(touchPos);
        return touchPos;
    }

    private void finishAttack() {
        attackCharging = false;
        if (!controller.sendAttack(fallBackAttackPoint, attackElapsedTime))
            player.sadFinish();
    }

    public void loseLife() {
        lifeBar.loseLife();
    }

    public int getLives() {
        return lifeBar.getChildren().size;
    }

    public void finishGame() {
        die = true;

        Image gameOverImage = new Image(new Texture("game_over.png"));
        gameOverImage.setSize(Main.GAME_WIDTH / 3f, Main.GAME_HEIGHT / 3f);
        gameOverImage.setPosition(Main.GAME_WIDTH / 2f - Main.GAME_WIDTH / 3f / 2f, Main.GAME_HEIGHT / 2f);
        UIStage.addActor(gameOverImage);

        Label title = new Label("Thy name archmage", Main.SKIN, "default");
        title.setSize(Main.GAME_WIDTH, Main.GAME_HEIGHT / 12f);
        title.setPosition(0, gameOverImage.getY() - 1.5f * Main.GAME_HEIGHT / 12f);
        title.setAlignment(Align.center);
        title.setColor(Color.RED);
        UIStage.addActor(title);

        final TextField name = new TextField("", Main.SKIN);
        name.setSize(Main.GAME_WIDTH / 4f, Main.GAME_HEIGHT / 12f);
        name.setPosition(Main.GAME_WIDTH / 2f - Main.GAME_WIDTH / 4f / 2f, title.getY() - .85f * Main.GAME_HEIGHT / 12f);
        UIStage.addActor(name);

        Button button = new Button(Main.SKIN, "close");
        button.setSize(Main.GAME_WIDTH / 12f, Main.GAME_HEIGHT / 12f);
        button.setPosition(Main.GAME_WIDTH / 2f - Main.GAME_WIDTH / 12f / 2, name.getY() - 1.5f * Main.GAME_HEIGHT / 12f);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.saveScore(name.getText(), score.getScore());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        UIStage.addActor(button);
    }
}
