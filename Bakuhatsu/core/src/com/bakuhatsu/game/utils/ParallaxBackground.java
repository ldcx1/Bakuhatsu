package com.bakuhatsu.game.utils;

import com.bakuhatsu.game.Main;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.bakuhatsu.game.Screens.GameScreen;

public class ParallaxBackground extends Actor {

    private float scrollX;
    private float scrollY;
    private final Array<Texture> layers;
    private static final int LAYER_SPEED_DIFFERENCE = 2;

    private float x;
    private float y;
    private float width;
    private float height;

    private float speedX;
    private float speedY;
    private float offSrcX;
    private float offSrcY;

    public ParallaxBackground(Array<Texture> textures){
        layers = textures;

        for(int i = 0; i <textures.size; i++)
            layers.get(i).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.Repeat);
    }

    public void setSpeed(float speedX, float speedY){
        this.speedX = speedX;
        this.speedY = speedY;
    }

    public void setSpeedX(float speedX){
        this.speedX = speedX;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        scrollX += speedX;
        scrollY += speedY;
        for(int i = 0; i < layers.size; i++) {
            int srcX = (int) (scrollX + i * LAYER_SPEED_DIFFERENCE * scrollX);
            int srcY = (int) (scrollY + i * LAYER_SPEED_DIFFERENCE * scrollY);;
            batch.draw(layers.get(i), x, y, 0, 0, width, height,1, 1, 0, (int)(x + offSrcX) + srcX, (int)(y + offSrcY)+ srcY, (int) (layers.get(i).getWidth() * (width / getWidth())), (int) (layers.get(i).getHeight() * ( height / getHeight())), false, false);
        }
    }

    public void setOffset(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setOffsetSrc(float x, float y) {
        this.offSrcX = x;
        this.offSrcY = y;
    }

}