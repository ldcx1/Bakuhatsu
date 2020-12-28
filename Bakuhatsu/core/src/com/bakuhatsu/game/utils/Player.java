package com.bakuhatsu.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.bakuhatsu.game.Screens.GameScreen;

public class Player extends AnimatedActor{
    public static final float MIN_CHARGE_TIME = 0.1f;
    private final AnimatedActor attackHand;
    private Projectile projectile;
    public final static float MAX_CHARGE_TIME = 3.5f;

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height, 0, 0);
        TextureRegion[][] textureRegionsIdle = TextureRegion.split(new Texture("Player/Idle.png"), 150, 150);
        Array<TextureRegion> regionsIdle = new Array<>(textureRegionsIdle.length * textureRegionsIdle[0].length);
        for (TextureRegion[] regions : textureRegionsIdle)
            for (TextureRegion region : regions)
                regionsIdle.add(region);

        addAnimation("Idle", new Animation<TextureRegion>(.1f, regionsIdle, Animation.PlayMode.LOOP));

        TextureRegion[][] textureRegionsAttack = TextureRegion.split(new Texture("Player/Attack.png"), 150, 150);
        Array<TextureRegion> regionsAttack = new Array<>(textureRegionsAttack.length * textureRegionsAttack[0].length);
        for (TextureRegion[] textureRegions : textureRegionsAttack)
            for (TextureRegion textureRegion : textureRegions)
                regionsAttack.add(textureRegion);

        addAnimation("Attack", new Animation<TextureRegion>(.1f, regionsAttack, Animation.PlayMode.LOOP));

        TextureRegion[][] textureRegionsDeath = TextureRegion.split(new Texture("Player/Death.png"), 150, 150);
        Array<TextureRegion> regionsDeath = new Array<>(textureRegionsDeath.length * textureRegionsDeath[0].length);
        for (TextureRegion[] textureRegions : textureRegionsDeath)
            for (TextureRegion textureRegion : textureRegions)
                regionsDeath.add(textureRegion);

        addAnimation("Death", new Animation<TextureRegion>(2f, regionsDeath, Animation.PlayMode.NORMAL));
        setAnimation("Idle");

        attackHand = new AnimatedActor(x, y, 150, 150, 70, 150-66);
        TextureRegion[][] textureRegionsHandAttack = TextureRegion.split(new Texture("Player/HandAttack.png"), 150, 150);
        Array<TextureRegion> regionsHandAttack = new Array<>(textureRegionsHandAttack.length * textureRegionsHandAttack[0].length);
        for (TextureRegion[] textureRegions : textureRegionsHandAttack)
            for (TextureRegion textureRegion : textureRegions)
                regionsHandAttack.add(textureRegion);

        attackHand.addAnimation("Attack", new Animation<TextureRegion>(.1f, regionsHandAttack, Animation.PlayMode.LOOP));
        attackHand.setAnimation("Attack");
        attackHand.setVisible(false);
    }

    public Player(float x, float y) {
        this(x, y, 150, 150);
    }

    public void setParent(Stage stage) {
        stage.addActor(this);
        stage.addActor(attackHand);
    }

    @Override
    public void setRotation(float angle) {
        attackHand.setRotation((float) -Math.toDegrees(angle) + 10);
        Vector2 position = getLauncherPosition().add(new Vector2((float) Math.cos(-angle), (float) Math.sin(-angle)).scl(55f));
        projectile.setPosition(position.x - projectile.getWidth() / 2, position.y - projectile.getHeight() / 2);
    }

    public void prepareAttack(Projectile projectile) {
        this.projectile = projectile;
        this.setAnimation("Attack");
        attackHand.setVisible(true);
        projectile.setScale(0);
        attackHand.setZIndex(projectile.getZIndex() + 1);
    }

    public void finishAttack(float angle, float velocityX, float velocityY, int damage, float attackElapsedTime) {
        this.setAnimation("Idle");
        attackHand.setVisible(false);
        projectile.constructBody(angle, velocityX, velocityY, damage, attackElapsedTime * 0.5f);
    }

    public void constructAttack(float elapsedTime) {
        projectile.setScale(elapsedTime * 0.5f);
    }

    public Vector2 getLauncherPosition() {
        return new Vector2(attackHand.getX() + attackHand.getOriginX(), attackHand.getY() + attackHand.getOriginY());
    }

    public void die(float delta) {
        if(getY() + 50 > (GameScreen.WALL_POSITION_DOWN.y + GameScreen.WALL_WIDTH))
            setPosition(getX(), getY() - delta * 50);
        else finishDeath();
    }

    public void finishDeath() {
        setAnimation("Death");
    }

    public void sadFinish() {
        this.setAnimation("Idle");
        attackHand.setVisible(false);
        projectile.sadDestroy();
    }
}
