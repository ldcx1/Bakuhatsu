package com.bakuhatsu.game.utils;

import com.bakuhatsu.game.Main;
import com.bakuhatsu.game.Model;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Projectile extends AnimatedActor implements Destructible{
    private final ParticleEffect particleEffect;
    private final World world;
    private Body body;

    private boolean charging = true;
    private boolean delete;

    private float scaleFactor;
    private int damage;

    private static final Animation<TextureRegion> FIREBALL_ANIMATION;
    private static final Animation<TextureRegion> EXPLOSION_ANIMATION;

    private static final float EXPLOSION_HEIGHT = 66;
    private static final float EXPLOSION_WIDTH = 81;

    public static final int MAX_DAMAGE = 20000;


    static {
        Array<TextureRegion> regions = new Array<>();
        regions.add(new TextureRegion(new Texture("Projectile/fireball1.png")));
        regions.add(new TextureRegion(new Texture("Projectile/fireball2.png")));
        regions.add(new TextureRegion(new Texture("Projectile/fireball3.png")));
        FIREBALL_ANIMATION = new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.LOOP);

        Array<TextureRegion> regionsExplosion = new Array<>();
        for(int i = 1; i <= 7 ; ++i)
            regionsExplosion.add(new TextureRegion(new Texture("Projectile/Explosion/exp"+i+".png")));

        EXPLOSION_ANIMATION = new Animation<TextureRegion>(0.1f, regionsExplosion, Animation.PlayMode.NORMAL);
    }

    public Projectile(World world, float x, float y) {
        this(world, new Vector2(x, y));
    }

    public Projectile(World world, Vector2 position) {
        super(position.x, position.y, 30, 30, 15, 15);
        this.world = world;

        position.add( new Vector2((float) Math.cos(0), (float) Math.sin(0)).scl(55f));
        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);

        addAnimation("Idle", FIREBALL_ANIMATION);
        setAnimation("Idle");

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("Projectile/flames.party"), Gdx.files.internal("Projectile"));
        particleEffect.getEmitters().first().setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        particleEffect.getEmitters().first().setContinuous(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(!charging && !world.isLocked()) {
            if (delete) {
                if (body != null)
                    world.destroyBody(body);
                this.remove();
            }

            Vector2 velocity = body.getLinearVelocityFromWorldPoint(body.getWorldCenter());
            body.setTransform(body.getPosition(), Model.calculateRotationMaxPI2(velocity.x, velocity.y, 0, 0));
            this.setRotation(Model.calculateRotationMaxPI2(velocity.x, velocity.y, 0, 0) * MathUtils.radiansToDegrees);
            this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
            particleEffect.getEmitters().first().setPosition(body.getPosition().x, body.getPosition().y);

            float rotation = velocity.angleDeg() + 180;
            ParticleEmitter.ScaledNumericValue val = particleEffect.getEmitters().first().getAngle();
            float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
            float h1 = rotation + amplitude;
            float h2 = rotation - amplitude;
            val.setHigh(h1, h2);
            particleEffect.getEmitters().first().getAngle().setLow(rotation);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(!charging) {
            particleEffect.update(Gdx.graphics.getDeltaTime());
            particleEffect.draw(batch);
        }
    }

    public void sadDestroy() {
        remove();
    }

    @Override
    public void destroy(final float x, final float y, final float rotation) {
        delete = true;

        AnimatedActor explosion = new AnimatedActor(
                x - EXPLOSION_WIDTH / 2 + (float) Math.cos(rotation) * EXPLOSION_WIDTH / 2 * scaleFactor,
                y - EXPLOSION_HEIGHT / 2 + (float) Math.sin(rotation) * EXPLOSION_HEIGHT / 2 * scaleFactor,
                EXPLOSION_WIDTH, EXPLOSION_HEIGHT,
                EXPLOSION_WIDTH / 2, EXPLOSION_HEIGHT / 2, true);

        explosion.setScale(scaleFactor);
        explosion.setRotation(-90 + (float)Math.toDegrees(rotation));
        explosion.addAnimation("Idle", EXPLOSION_ANIMATION);
        explosion.setAnimation("Idle");
        getParent().addActor(explosion);
    }

    public int getDamage() {
        int result =  (int) ((scaleFactor * damage * body.getLinearVelocityFromWorldPoint(body.getWorldCenter()).len2()) * Target.MAX_LIFE) / MAX_DAMAGE;
        Gdx.app.log("Damage", String.valueOf(result));
        return result;
    }

    public void constructBody(float angle, float velocityX, float velocityY, int damage, float scaleFactor) {
        this.damage = damage;
        particleEffect.start();

        BodyDef bd = new BodyDef();
        bd.position.set(this.getX() , this.getY());
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.bullet = true;
        bd.active = true;
        body = world.createBody(bd);

        //PolygonShape shapeShaft = new PolygonShape();
        //shapeShaft.setAsBox(getWidth()/2, getHeight() / 8);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius((getWidth() / 2  - 4)* scaleFactor);

        FixtureDef fixtureShaft = new FixtureDef();
        fixtureShaft.shape = circleShape;
        fixtureShaft.density = 6.5f;
        fixtureShaft.restitution = 0.4f;
        body.createFixture(fixtureShaft);

        //        PolygonShape shapePoint = new PolygonShape();
        //        float[] shapePointV = {
        //                getWidth() / 2, getHeight(),
        //                getWidth() / 2 * 1.5f, 0,
        //                getWidth() / 2, - getHeight()};
        //        shapePoint.set(shapePointV);
        //
        //        FixtureDef fixturePoint = new FixtureDef();
        //        fixturePoint.shape = shapePoint;
        //        fixturePoint.density = 26.0f;
        //        fixturePoint.restitution = 0.05f;
        //        fixturePoint.friction = 0.5f;
        //        body.createFixture(fixturePoint);

        body.setUserData(this);
        body.setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, angle);
        body.setLinearVelocity(velocityX, velocityY);

        particleEffect.scaleEffect(scaleFactor);
        this.scaleFactor = scaleFactor;
        charging = false;
    }

    public void setBodyMovement(float velocityX, float velocityY, float x, float y, float angle) {
        body.setLinearVelocity(new Vector2(velocityX, velocityY));
        body.setTransform(x,y, angle);
    }

}
