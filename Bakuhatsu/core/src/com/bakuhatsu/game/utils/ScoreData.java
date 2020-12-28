package com.bakuhatsu.game.utils;

import com.badlogic.gdx.Gdx;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreData implements Comparable<ScoreData> {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private final String name;
    private final int score;
    private Date date;

    public ScoreData(String line) {
        String[] data = line.split("\\|");
        name = data[0];
        score = Integer.parseInt(data[1]);
        try {
            date = DATE_FORMAT.parse(data[2]);
        }
        catch (ParseException e) {
            Gdx.app.error("Parse", e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score + "";
    }

    public String getDate() {
        return DATE_FORMAT.format(date);
    }

    @Override
    public int compareTo(ScoreData o) {
        if(this.score > o.score)
            return -1;
        if(this.score < o.score)
            return  1;
        if(this.name.compareTo(o.name) != 0)
            return this.name.compareTo(o.name);
        return this.date.compareTo(o.date);
    }
}
