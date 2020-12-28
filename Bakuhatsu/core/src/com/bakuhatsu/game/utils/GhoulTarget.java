package com.bakuhatsu.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GhoulTarget extends Target{
    public GhoulTarget(World world, float x, float y, float angle, float velocityX, float velocityY, int score, int life) {
        super(world, x, y,28f - 22 - 10f / 2, - (30f - 20 - 39/2f), 10f, 39f, 57, 60, 57/2f, 60/2f, angle, velocityX, velocityY, score, life, TargetType.Ghoul);

        Array<TextureRegion> regions = new Array<>();
        for(int i = 1; i <= 7 ; ++i)
            regions.add(new TextureRegion(new Texture("Enemy/Ghoul/burning-ghoul"+i+".png")));

        addAnimation("Idle", new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.LOOP));
        setAnimation("Idle");
    }

    @Override
    public float getSpeedFactor() {
        return 1.3f;
    }
}
