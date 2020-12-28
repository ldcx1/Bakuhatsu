package com.bakuhatsu.game;

import com.badlogic.gdx.Gdx;
import com.bakuhatsu.game.Screens.GameScreen;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.bakuhatsu.game.utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class Model implements ContactListener {
    private Controller controller;
    private final Random random;
    private final long startTime;
    public Model() {
        startTime = System.currentTimeMillis();
        random = new RandomXS128();
    }

    public float getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void saveScore(String name, String score) {
        try {
            FileWriter fw = new FileWriter(Main.scoreFile, true);
            fw.write(name + "|" + score + "|" + ScoreData.DATE_FORMAT.format(new Date()) + "\n");
            fw.close();
        } catch (IOException ioe) {
            Gdx.app.log("IOException", ioe.getMessage());
        }
    }

    public static float calculateRotation(float beginX, float beginY, float endX, float endY) {
        return (float) Math.atan2(beginY - endY, beginX - endX);
    }

    public static float calculateRotationDegrees(float beginX, float beginY, float endX, float endY) {
        return (float) Math.toDegrees(Math.atan2(beginY - endY, beginX - endX));
    }

    public static float calculateRotationMaxPI2(float beginX, float beginY, float endX, float endY) {
        float theta = (calculateRotation(beginX, beginY, endX, endY));
        if(theta < - Math.PI / 2)
            return (float) (- Math.PI / 2);
        return (float) Math.min(theta, Math.PI / 2);
    }

    private float convertNormalToAngle(Vector2 normal) {
        return calculateRotation(normal.x, normal.y, 0, 0);
    }

    public boolean launchProjectile(float beginX, float beginY, float endX, float endY, float elapsedTime) {
        float angle = calculateRotationMaxPI2(beginX, beginY, endX, endY);
        float velocity = Vector2.dst(beginX, beginY, endX, endY) * 200;

        if(elapsedTime < Player.MIN_CHARGE_TIME || velocity < 10)
            return false;

        float velocityX = (float) (Math.sqrt(velocity) * Math.cos(-angle));
        float velocityY = (float) (Math.sqrt(velocity) * Math.sin(-angle));

        controller.launchProjectile(-angle, velocityX, velocityY, 1);
        return true;
    }

    private Vector2 generateTargetPosition(TargetType type) {
        switch(type) {
            case Angel: return new Vector2(GameScreen.WALL_LENGTH - 400 + 100 * random.nextFloat(), GameScreen.WALL_POSITION_DOWN.y + GameScreen.WALL_WIDTH + 150 + random.nextFloat() * (Main.GAME_HEIGHT - 260));
            case Ghoul: return new Vector2(GameScreen.WALL_LENGTH - 400 + 100 * random.nextFloat() ,GameScreen.WALL_POSITION_DOWN.y + GameScreen.WALL_WIDTH);
            default: return new Vector2(0,0);
        }
    }
    private Vector2 generateTargetVelocity(TargetType type) {
        return new Vector2(- (random.nextFloat() * 20 +  2 + getElapsedTime() * 0.01f), 0);
    }
    private float generateTargetAngle(TargetType type) {
        return 0;
    }
    private Vector2 generateTargetScoreLife(TargetType type, Vector2 velocity) {
        return new Vector2(velocity.len() * 7, Target.MAX_LIFE / (velocity.len() / 3));
    }

    public void createTarget(float delta) {
        TargetType type = TargetType.values()[random.nextInt(TargetType.values().length)];
        Vector2 position = generateTargetPosition(type);
        Vector2 velocity = generateTargetVelocity(type);
        Vector2 attributes = generateTargetScoreLife(type, velocity);
        controller.createTarget(type, position.x, position.y, generateTargetAngle(type), velocity.x, velocity.y, (int)attributes.x, (int)attributes.y);
    }

    public void recycleTarget(Target target) {
        Vector2 position = generateTargetPosition(target.getTargetType());
        Vector2 velocity = generateTargetVelocity(target.getTargetType());
        Vector2 attributes = generateTargetScoreLife(target.getTargetType(), velocity);
        target.resetBody(position, velocity, generateTargetAngle(target.getTargetType()), (int)attributes.x, (int)attributes.y);
    }

    @Override
    public void beginContact(Contact contact) {
        Object classAObj = contact.getFixtureA().getBody().getUserData();
        Object classBObj = contact.getFixtureB().getBody().getUserData();

        if(classAObj instanceof Target && classBObj instanceof Target) {
            Gdx.app.log("Collider", "Box2D u k??");
            return;
        }

        if(classAObj instanceof Projectile && classBObj instanceof Projectile)
            return;

        Wall wall = null;
        Destructible collider = null;

        if (classAObj instanceof Wall) {
            wall = (Wall) classAObj;
            collider = (Destructible) classBObj;
        }
        else if (classBObj instanceof  Wall) {
            wall = (Wall) classBObj;
            collider = (Destructible) classAObj;
        }

        if(wall != null) {
            switch (wall.getWallType()) {
                case Platform: if(collider instanceof Target) break;
                case Score: if(collider instanceof Target) {
                                controller.loseLife();
                                controller.prepareToInvokeTarget();
                            }
                case Destructive: {
                    if(collider instanceof Target)
                        recycleTarget((Target) collider);
                    else
                        collider.destroy(contact.getWorldManifold().getPoints()[0].x, contact.getWorldManifold().getPoints()[0].y, convertNormalToAngle(contact.getWorldManifold().getNormal()));
                }
            }
        }
        else {
            Projectile projectile = null;
            Target target = null;
            if(classAObj instanceof Projectile) {
                projectile = (Projectile) classAObj;
                target = (Target) classBObj;
            }
            else {
                projectile = (Projectile) classBObj;
                target = (Target) classAObj;
            }

            target.takeDamage(projectile.getDamage());
            if (target.getLife() <= 0) {
                controller.addScore(target.getScore());
                recycleTarget(target);
            }

            projectile.destroy(contact.getWorldManifold().getPoints()[0].x, contact.getWorldManifold().getPoints()[0].y, convertNormalToAngle(contact.getWorldManifold().getNormal()));
        }
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
