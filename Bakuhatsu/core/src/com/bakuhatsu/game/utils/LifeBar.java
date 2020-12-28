package com.bakuhatsu.game.utils;

import com.bakuhatsu.game.Main;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LifeBar extends Group {
    private static final int LIVES_COUNT = 3;
    private static final Texture TEXTURE = new Texture("heart.png");
    private static final float SIZE = 20;

    public LifeBar() {
        for(int i = 0; i < LIVES_COUNT; ++i)
            addLife();
    }
    public void addLife() {
        Image image = new Image(TEXTURE);
        image.setSize(SIZE, SIZE + SIZE / 3);
        image.setPosition(getChildren().size * SIZE + (getChildren().size + 1) * SIZE / 3 , Main.GAME_HEIGHT- 1.5f * SIZE);
        addActor(image);
    }

    public void loseLife() {
        if(getChildren().size > 0)
            removeActor(getChild(getChildren().size - 1));
    }
}
