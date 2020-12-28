package com.bakuhatsu.game;

import com.badlogic.gdx.Gdx;
import com.bakuhatsu.game.utils.ScoreData;
import com.bakuhatsu.game.utils.TargetType;
import com.badlogic.gdx.math.Vector3;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Controller {
    private final View view;
    private final Model model;

    private float beginX, beginY;
    private boolean pendingTarget = false;
    private boolean pendingProjectile = false;

    public Controller(View view, Model model) {
        this.model = model;
        this.view = view;

        model.setController(this);
        changeToMainScreen();

    }

    public void changeToMainScreen() {
        view.showMenuScreen(this);
    }
    public void changeToGameScreen() {
        view.showGameScreen(this);
    }
    public void changeToScoreScreen(boolean showLast) {
        view.showScoreboardScreen(this, showLast);
    }
    public void exitCall() {
        Gdx.app.exit();
    }

    public void beginAttack(Vector3 coordinates) {
        beginX = coordinates.x;
        beginY = coordinates.y;
        view.beginAttack();
    }

    public void dragInput(Vector3 coordinates) {
        view.setArmRotation(Model.calculateRotationMaxPI2(beginX, beginY, coordinates.x, coordinates.y));
    }

    public boolean sendAttack(Vector3 coordinates, float elapsedTime) {
        return model.launchProjectile(beginX, beginY, coordinates.x, coordinates.y, elapsedTime);
    }
    public void launchProjectile(float angle, float velocityX, float velocityY, int damage) {
        pendingProjectile = true;
        view.launchProjectile(angle, velocityX, velocityY, damage);
    }
    public void createTarget(TargetType type, float x, float y, float angle, float velocityX, float velocityY, int score, int life) {
        view.createTarget(type ,x, y, angle, velocityX, velocityY, score, life);
    }
    public void invokeTarget(float delta) {
        pendingTarget = false;
        model.createTarget(delta);
    }

    public void addScore(int score) {
        view.addScore(score);
    }

    public void targetDied() {
        view.targetDied();
    }

    public Model getModel() {
        return model;
    }

    public void loseLife() {
        view.loseLife();
        if(view.getLives() == 0)
            view.finishGame();
    }

    public void prepareToInvokeTarget() {
        pendingTarget = true;
    }

    public boolean pendingToInvokeProjectile() {
        return pendingProjectile;
    }

    public void setFlagProjectile() {
        pendingProjectile = false;
    }

    public boolean pendingToInvokeTarget() {
        return pendingTarget;
    }

    public void saveScore(String name, String score) {
        if(name.length() > 0) {
            model.saveScore(name, score);
            changeToScoreScreen(true);
        }
        else changeToMainScreen();
    }
}
