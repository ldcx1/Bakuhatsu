package com.bakuhatsu.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Target extends AnimatedActor implements Destructible{
    public static final int MAX_LIFE = 200;

    private boolean isRecycled = false;
    private boolean delete;

    private int life, initialLife;
    private Vector2 position;
    private float angle;
    private int score;

    protected final World world;
    protected final Body body;

    private final TargetType targetType;
    private final float offsetX;
    private final float offsetY;

    private Vector2 velocity;

    public Target(World world, float x, float y, float boxOffsetX, float boxOffsetY, float boxWidth, float boxHeight, float width, float height, float originX, float originY, TargetType targetType) {
        super(x, y,width, height, originX, originY);
        this.world = world;
        this.targetType = targetType;

        offsetX = boxOffsetX;
        offsetY = boxOffsetY;

        BodyDef bd = new BodyDef();
        bd.position.set(x, y );
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.active = true;
        body = world.createBody(bd);

        PolygonShape collisionBox = new PolygonShape();
        collisionBox.setAsBox(boxWidth / 2 , boxHeight / 2);

        FixtureDef fd = new FixtureDef();
        fd.density = 800000;
        fd.friction = 0.5f;
        fd.restitution = 0.9f;
        fd.shape = collisionBox;
        //fd.filter.groupIndex = -4;
        fd.filter.categoryBits = 0x0004;
        fd.filter.maskBits = ~0x0004;

        body.createFixture(fd);
        body.setUserData(this);
        collisionBox.dispose();
    }

    public Target(World world, float x, float y, float boxOffsetX, float boxOffsetY, float boxWidth, float boxHeight, float width, float height, float originX, float originY, float angle, float velocityX, float velocityY, int score, int life, TargetType targetType) {
        this(world, x, y, boxOffsetX, boxOffsetY, boxWidth, boxHeight, width, height, originX, originY, targetType);
        this.resetBody(new Vector2(x, y), new Vector2(velocityX, velocityY), angle, score, life);
        //MassData massData = body.getMassData();
        //massData.center.set(getWidth(), getHeight() / 2);
        //massData.mass = 50000f;
        //body.setMassData(massData);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(!world.isLocked()) {
            if (delete) {
                world.destroyBody(body);
                this.remove();
            }

            if (isRecycled) {
                body.setTransform(position, angle);
                body.setLinearVelocity(velocity);
                isRecycled = false;
                this.setAnimationSpeed(1 / velocity.len() * getSpeedFactor());
            }

            this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
            this.setPosition(body.getPosition().x - this.getWidth() / 2 + offsetX, body.getPosition().y - this.getHeight() / 2 + offsetY);

            if(body.getAngle() != 0) {
                body.setTransform(body.getPosition(), 0);
            }

            Vector2 currentVelocity = body.getLinearVelocity();
            body.setLinearVelocity(currentVelocity.x - 0.5f * (Math.signum(currentVelocity.x - velocity.x)), 0);
        }
    }

    @Override
    public void destroy(float x, float y, float rotation){
        delete = true;
    }

    public int getScore() {
        return score;
    }

    public void takeDamage(int damage) {
        life -= damage;
        Gdx.app.log("Enemy life", life+" / " + initialLife + " damage " + damage);
    }

    public void destroy() {
        destroy(0, 0, 0);
    }

    public int getLife() {
        return life;
    }

    @Override
    public float getRedIndex() {
        return 1 - (float)life / initialLife * 1;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void resetBody(Vector2 position, Vector2 velocity, float angle, int score, int life) {
        this.setRotation((float) Math.toDegrees(angle));
        this.position = position;
        this.angle = angle;
        this.velocity = velocity;
        this.score = score;
        this.initialLife = this.life = life;
        isRecycled = true;
    }

    public float getSpeedFactor() {
        return 1;
    }
}

