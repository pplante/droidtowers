package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.utils.Random;
import sun.rmi.runtime.Log;

import static com.unhappyrobot.utils.Random.*;

public class Asteroid extends GameObject {
    public static String[] ASTEROID_TEXTURES = {
            "asteroids/big-01.png",
            "asteroids/mid-01.png",
            "asteroids/small-01.png",
    };

    public Asteroid(float x, float y, float radius) {
        super(x, y);
        physicsShape = PhysicsShapes.CIRCLE;
        setRadius(radius);
//        setLinearVelocity(new Vector2(randomFloat(), randomFloat()));

        useTexture(ASTEROID_TEXTURES[randomInt(ASTEROID_TEXTURES.length)]);
    }
}
