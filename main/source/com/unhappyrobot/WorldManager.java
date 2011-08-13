package com.unhappyrobot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.unhappyrobot.entities.GameObject;

import java.util.HashMap;

public class WorldManager {
    private static WorldManager instance;
    private World world;
    private CircleShape circleShape;

    private WorldManager() {
        world = new World(new Vector2(0, 0), true);

        circleShape = new CircleShape();
        circleShape.setRadius(30);
    }

    public static WorldManager getInstance() {
        if(instance == null) {
            instance = new WorldManager();
        }

        return instance;
    }

    public static World getWorldInstance() {
        return WorldManager.getInstance().world;
    }

    public static void update(float timeDelta) {
        getWorldInstance().step(timeDelta, 1, 1);
    }

    public static Body addGameObject(GameObject gameObject) {
        WorldManager worldManager = WorldManager.getInstance();
        World world = worldManager.world;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.x = gameObject.getPosition().x;
        def.position.y = gameObject.getPosition().y;


        Body worldBody = world.createBody(def);
        worldBody.createFixture(worldManager.circleShape, 1);

        return worldBody;
    }
}
