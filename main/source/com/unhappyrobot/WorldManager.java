package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.physics.PhysicsBody;
import com.unhappyrobot.physics.PhysicsBodyPoint;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {
    private static WorldManager instance;
    public static final float PIXEL_TO_METER_RATIO = 32.0f;
    private World world;
    private CircleShape circleShape;
    private List<GameObject> gameObjects;
    private float lastRunTime;

    private WorldManager() {
        world = new World(new Vector2(0, 0), true);
        gameObjects = new ArrayList<GameObject>();
        lastRunTime = 0.0f;
    }

    public static WorldManager getInstance() {
        if (instance == null) {
            instance = new WorldManager();
        }

        return instance;
    }

    public static World getWorldInstance() {
        return WorldManager.getInstance().world;
    }

    public static void update() {
        float timeDelta = DeferredManager.onGameThread().getCurrentTime() - getInstance().lastRunTime;
        getInstance().lastRunTime = DeferredManager.onGameThread().getCurrentTime();

        getWorldInstance().step(timeDelta, 1, 1);
    }

    public static Body addGameObject(GameObject gameObject, PhysicsBody physicsBody) {
        WorldManager worldManager = WorldManager.getInstance();
        World world = worldManager.world;

        BodyDef def = new BodyDef();
        def.allowSleep = true;
        def.fixedRotation = true;
        def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(gameObject.getPosition());

        Body worldBody = world.createBody(def);

        switch (gameObject.getPhysicsShape()) {
            case POLYGON:

                if (physicsBody != null) {
                    for (PhysicsBodyPoint point : physicsBody.getPoints()) {
                        addPolygonToWorldBody(worldBody, point.getVectorData(gameObject.getOrigin(), gameObject.getScale()), 1.0f);
                    }
                } else {
                    PolygonShape poly = new PolygonShape();
                    Vector2 gameObjectSize = gameObject.getSize();
                    poly.setAsBox(gameObjectSize.x, gameObjectSize.y);
                    worldBody.createFixture(poly, 1);
                    poly.dispose();
                }
                break;
            case CIRCLE:
                CircleShape shape = new CircleShape();
                shape.setRadius(gameObject.getRadius() * 10.0f);
                worldBody.createFixture(shape, 1);
                shape.dispose();
                break;
        }

        worldManager.gameObjects.add(gameObject);


        return worldBody;
    }

    private static void addPolygonToWorldBody(Body worldBody, Vector2[] vectorData, float density) {
        PolygonShape shape = new PolygonShape();
        shape.set(vectorData);

        worldBody.createFixture(shape, density);

        shape.dispose();
    }

    public static Vector2 pixelsToMeters(Vector2 pixelSpace) {
        Vector2 meterSpace = new Vector2(pixelSpace);
        meterSpace.x = pixelSpace.x / PIXEL_TO_METER_RATIO;
        meterSpace.y = pixelSpace.y / PIXEL_TO_METER_RATIO;

        return meterSpace;
    }

    public static boolean isLocked() {
        return getInstance().world.isLocked();
    }
}
