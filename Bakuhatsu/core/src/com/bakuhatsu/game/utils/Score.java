package com.bakuhatsu.game.utils;

import com.badlogic.gdx.utils.Align;
import com.bakuhatsu.game.Main;
import com.bakuhatsu.game.View;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Score extends Group {

    private final Label scoreNumber;

    public Score()
    {
        scoreNumber = new Label("0", Main.SKIN);
        scoreNumber.setSize(6 *  Main.GAME_WIDTH / 100f, Main.GAME_WIDTH / 100f);
        scoreNumber.setPosition(Main.GAME_WIDTH - scoreNumber.getWidth() - Main.GAME_WIDTH / 100f, Main.GAME_WIDTH / 75f);
        scoreNumber.setAlignment(Align.right);
        addActor(scoreNumber);
    }

    public void increaseScore(int amount){
        scoreNumber.setText(Integer.parseInt(String.valueOf(scoreNumber.getText())) + amount);
    }

    public String getScore() {
        return String.valueOf(scoreNumber.getText());
    }
}
