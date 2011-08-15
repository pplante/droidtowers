package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.unhappyrobot.entities.GameObject;

import java.util.ArrayList;
import java.util.List;

import static com.unhappyrobot.entities.GameObject.PhysicsShapes;
import static com.unhappyrobot.entities.GameObject.PhysicsShapes.*;

public class WorldManager {
    private static WorldManager instance;
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

        for (GameObject gameObject : getInstance().gameObjects) {
            gameObject.beforePhysicsUpdate();
        }

        getWorldInstance().step(timeDelta, 1, 1);

        for (GameObject gameObject : getInstance().gameObjects) {
            gameObject.afterPhysicsUpdate();
        }
    }

    public static Body addGameObject(GameObject gameObject) {
        WorldManager worldManager = WorldManager.getInstance();
        World world = worldManager.world;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.x = gameObject.getPosition().x;
        def.position.y = gameObject.getPosition().y;

        Body worldBody = world.createBody(def);

        Shape objectShape = null;
        switch (gameObject.getPhysicsShape()) {
            case POLYGON:
                objectShape = new PolygonShape();
                ((PolygonShape)objectShape).setAsBox(gameObject.getSize().x, gameObject.getSize().y);
                break;
            case CIRCLE:
                objectShape = new CircleShape();
                objectShape.setRadius(gameObject.getRadius());
                break;
        }

        worldBody.createFixture(objectShape, 1);
        objectShape.dispose();

        worldManager.gameObjects.add(gameObject);


        return worldBody;
    }
}
