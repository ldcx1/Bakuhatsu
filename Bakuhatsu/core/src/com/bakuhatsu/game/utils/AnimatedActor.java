package com.bakuhatsu.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.Dictionary;
import java.util.Hashtable;

public class AnimatedActor extends Actor {
    private final Dictionary<String, Animation<TextureRegion>> animations;
    private String currentAnimation;
    private final boolean destroyAtFinish;
    private float elapsed = 0;

    private static final ShaderProgram SHADER;
    static {
        SHADER = new ShaderProgram(
                Gdx.files.internal("vertex.glsl").readString(),
                Gdx.files.internal("fragment.glsl").readString()
        );
    }

    public AnimatedActor(float x, float y, float width, float height, float originX, float originY) {
        this(x, y, width, height, originX, originY,false);
    }

    public AnimatedActor(float x, float y, float width, float height, float originX, float originY, boolean destroyAtFinish) {
        animations = new Hashtable<>();
        this.setBounds(x, y, width, height);
        setOrigin(originX, originY);
        this.destroyAtFinish = destroyAtFinish;
    }

    void addAnimation(String name, Animation<TextureRegion> animation) {
        animations.put(name, animation);
    }

    void setAnimation(String name) {
        currentAnimation = name;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        TextureRegion current = animations.get(currentAnimation).getKeyFrame(elapsed, destroyAtFinish);

        SHADER.bind();
        SHADER.setUniformf("v_color", getRedIndex());

        batch.setShader(SHADER);
        batch.draw(current, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setShader(null);

        elapsed += Gdx.graphics.getDeltaTime();

        if(destroyAtFinish && animations.get(currentAnimation).isAnimationFinished(elapsed))
            remove();
    }

    void setAnimationSpeed(float speed) {
        animations.get(currentAnimation).setFrameDuration(speed);
    }

    public float getRedIndex() {
        return 0;
    }
}
