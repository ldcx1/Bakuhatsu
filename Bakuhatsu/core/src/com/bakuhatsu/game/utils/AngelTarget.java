package com.bakuhatsu.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class AngelTarget extends Target{

    public AngelTarget(World world, float x, float y, float angle, float velocityX, float velocityY, int score, int life) {
        super(world, x, y, 61f - 57f - 7f/2f, - (58f - 48f - 36f/2f),  7, 36, 122,117,122/2f,117/2f, angle, velocityX, velocityY, score, life, TargetType.Angel);
        Array<TextureRegion> regions = new Array<>();
        for(int i = 1; i <= 7 ; ++i)
            regions.add(new TextureRegion(new Texture("Enemy/Angel/angel"+i+".png")));

        addAnimation("Idle", new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.LOOP));
        setAnimation("Idle");
    }
}
