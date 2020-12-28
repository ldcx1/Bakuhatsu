package com.bakuhatsu.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Wall {
    private final WallType type;

    public Wall(World world, float x, float y, float width, float height, WallType wallType) {
        BodyDef bd = new BodyDef();
        bd.position.set(x + width / 2, y + height / 2);
        bd.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        Body body = world.createBody(bd);
        body.createFixture(shape, 0.0f);
        body.setUserData(this);
        type = wallType;
    }

    public Wall(World world, Vector2 position, float width, float height, WallType wallType) {
        this(world, position.x, position.y, width, height, wallType);
    }

    //use example
    //        int zece = -10;
    //        int doua = 10;
    //
    //        PolygonShape[] shapes = new PolygonShape[]{new PolygonShape(), new PolygonShape()};
    //        shapes[0].set(new Vector2[]{
    //                new Vector2(Gdx.graphics.getWidth() - doua + 300, doua),
    //                new Vector2(Gdx.graphics.getWidth() - doua + 300,Gdx.graphics.getHeight() - doua + 250),
    //                new Vector2(Gdx.graphics.getWidth() - zece + 300,Gdx.graphics.getHeight() - zece + 250),
    //                new Vector2(Gdx.graphics.getWidth() - zece + 300, zece)});
    //
    //        shapes[1].set(new Vector2[]{
    //                new Vector2(Gdx.graphics.getWidth() - doua +300,Gdx.graphics.getHeight() - doua + 250),
    //                new Vector2(- 150,Gdx.graphics.getHeight() - doua + 250),
    //                new Vector2( - 150,Gdx.graphics.getHeight() - zece +250),
    //                new Vector2(Gdx.graphics.getWidth() - zece + 300,Gdx.graphics.getHeight() - zece + 250)});
    //
    //        new Wall(world, WallType.Destructive, shapes);

    public Wall(World world, WallType wallType, Shape[] collisionShapes) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bd);

        for(Shape shape: collisionShapes)
            body.createFixture(shape, .0f);

        body.setUserData(this);
        type = wallType;
    }

    public WallType getWallType() {
        return type;
    }
}
