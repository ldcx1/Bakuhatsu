package com.bakuhatsu.game.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.bakuhatsu.game.Controller;
import com.bakuhatsu.game.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bakuhatsu.game.utils.Player;
import com.bakuhatsu.game.utils.ScoreData;

import javax.swing.table.TableColumn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ScoreScreen implements Screen {
    private final Stage stage;

    public ScoreScreen(final Controller controller, boolean showLast) {
        stage = new Stage(new StretchViewport(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        float heightFactor = Main.WINDOW_HEIGHT / 12f;
        float widthFactor = Main.WINDOW_WIDTH / 12f;

        Label title = new Label("Scoreboard", Main.SKIN, "default");
        title.setSize(Main.WINDOW_WIDTH, heightFactor);
        title.setPosition(0, Main.WINDOW_HEIGHT - heightFactor);
        title.setAlignment(Align.center);
        title.setColor(Color.WHITE);
        title.setFontScale(3);
        stage.addActor(title);

        Player player = new Player(- Main.WINDOW_HEIGHT/ 1.5f, -Main.WINDOW_HEIGHT/ 2f, Main.WINDOW_HEIGHT*2, Main.WINDOW_HEIGHT*2);
        stage.addActor(player);

        Player player2 = new Player(Main.WINDOW_WIDTH + Main.WINDOW_HEIGHT/ 1.5f, -Main.WINDOW_HEIGHT/ 2f, - Main.WINDOW_HEIGHT*2, Main.WINDOW_HEIGHT*2);
        stage.addActor(player2);


        Button back = new Button(Main.SKIN, "close");
        back.setSize(heightFactor, heightFactor);
        back.setPosition(Main.WINDOW_WIDTH / 2 - heightFactor / 2, 1);
        back.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.changeToMainScreen();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        stage.addActor(back);

        Table tableWrapper = new Table(Main.SKIN);
        tableWrapper.setSize(Main.WINDOW_WIDTH - widthFactor, Main.WINDOW_HEIGHT - 2 * heightFactor);
        tableWrapper.setPosition(heightFactor , heightFactor);
        tableWrapper.add("").expand().fill();
        tableWrapper.add(createTextBox("Place", Color.ROYAL));
        tableWrapper.add(createTextBox("Username", Color.ROYAL));
        tableWrapper.add(createTextBox("Score", Color.ROYAL));
        tableWrapper.add(createTextBox("Date", Color.ROYAL));
        tableWrapper.add("").expand().fill();

        tableWrapper.row().expand().fill();

        Table table = new Table(Main.SKIN);
        ScrollPane pane = new ScrollPane(table, Main.SKIN);

        tableWrapper.add("").expand().fill();
        tableWrapper.add(pane).colspan(5).expand().fill();

        stage.addActor(tableWrapper);
        List<ScoreData> dataList = new ArrayList<>();

        try {
            File file = new File(Main.scoreFile);
            if(!file.exists()) {
                if (!file.createNewFile())
                    controller.exitCall();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
                dataList.add(new ScoreData(line));
            br.close();
        }
        catch(IOException ioe) {
            Gdx.app.log("IOException", ioe.getMessage());
            controller.exitCall();
        }
        ScoreData inserted = null;
        if(showLast)
            inserted = dataList.get(dataList.size() - 1);

        Collections.sort(dataList);

        int index = 1;
        for (ScoreData data: dataList) {
            table.row();
            if(showLast && data.compareTo(inserted) == 0) {
                table.add(createTextBox((index++) + "", Color.CORAL));
                table.add(createTextBox(data.getName(), Color.CORAL));
                table.add(createTextBox(data.getScore(), Color.CORAL));
                table.add(createTextBox(data.getDate(), Color.CORAL));
            }
            else {
                table.add(createTextBox((index++) + "", Color.WHITE));
                table.add(createTextBox(data.getName(), Color.WHITE));
                table.add(createTextBox(data.getScore(), Color.WHITE));
                table.add(createTextBox(data.getDate(), Color.WHITE));
            }
            table.add("").expandX().fillX();
        }

        tableWrapper.row().expand().fill();;
        tableWrapper.add("").colspan(5).expand().fill();
    }

    @Override
    public void show() {
        Gdx.app.log("ScoreScreen","Show");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    private TextField createTextBox(String data, Color color) {
        TextField textField = new TextField(data, Main.SKIN);
        textField.setSize(Main.WINDOW_WIDTH / 4f, Main.WINDOW_HEIGHT / 8f);
        textField.setAlignment(Align.center);
        textField.setDisabled(true);
        textField.setColor(color);
        return textField;
    }
    private Label createEmptyLabel() {
        Label label = new Label(" ", Main.SKIN);
        label.setSize(Main.WINDOW_WIDTH / 4f * 4, Main.WINDOW_HEIGHT / 8f);
        label.setAlignment(Align.center);
        return label;
    }
}
