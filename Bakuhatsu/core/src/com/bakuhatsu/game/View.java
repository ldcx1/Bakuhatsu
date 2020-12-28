package com.bakuhatsu.game;

import com.bakuhatsu.game.Screens.GameScreen;
import com.bakuhatsu.game.Screens.MenuScreen;
import com.bakuhatsu.game.Screens.ScoreScreen;
import com.bakuhatsu.game.utils.TargetType;
import com.badlogic.gdx.Screen;

public class View {
    private final Main main;
    private Screen currentScreen;
    private GameScreen gameScreen;

    public View(Main main) {
        this.main = main;
    }

    public void showGameScreen(Controller controller) {
        currentScreen = new GameScreen(controller);
        main.setScreen(currentScreen);
        gameScreen = (GameScreen) currentScreen;
    }

    public void showMenuScreen(Controller controller) {
        gameScreen = null;
        currentScreen = new MenuScreen(controller);
        main.setScreen(currentScreen);
    }

    public void showScoreboardScreen(Controller controller, boolean showLast) {
        gameScreen = null;
        currentScreen = new ScoreScreen(controller, showLast);
        main.setScreen(currentScreen);
    }

    public void launchProjectile(float angle, float velocityX, float velocityY, int damage) {
        gameScreen.launchProjectile(angle, velocityX, velocityY, damage);
    }
    public void createTarget(TargetType type, float x, float y, float angle, float velocityX, float velocityY, int score, int life) {
        gameScreen.createTarget(type, x, y, angle, velocityX, velocityY, score, life);
    }

    public void setArmRotation(float rotation) {
        gameScreen.setArmRotation(rotation);
    }
    public void addScore(int score) {
        gameScreen.addScore(score);
    }
    public void targetDied() {
        gameScreen.enemyDied();
    }
    public void beginAttack() {
        gameScreen.beginAttack();
    }
    public void loseLife() {
        gameScreen.loseLife();
    }
    public int getLives() {
        return gameScreen.getLives();
    }
    public void finishGame() {
        gameScreen.finishGame();
    }
    public void resetCameraPosition() {
        gameScreen.resetCameraPosition();
    }
}
