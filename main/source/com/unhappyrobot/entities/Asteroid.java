package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.input.Action;
import com.unhappyrobot.input.InputManager;

import static com.unhappyrobot.utils.Random.randomFloat;
import static com.unhappyrobot.utils.Random.randomInt;

public class Asteroid extends GameObject {
    public static String[] ASTEROID_TEXTURES = {
            "asteroids/big-01",
            "asteroids/small-01",
    };

    public Asteroid(float x, float y, float radius) {
        super(x, y);
        setRadius(radius);
        setMass(1000000 / (radius * 100));

        String baseFilePath = ASTEROID_TEXTURES[randomInt(ASTEROID_TEXTURES.length)];

        useTexture(baseFilePath + ".png");
        loadPhysicsBodyFromJson(Gdx.files.internal(baseFilePath + ".json").readString());

        InputManager.listen(InputManager.Keys.B, new Action() {
            public void run(float timeDelta) {
                if (randomInt(50) < 25) {
                    addRotation(randomFloat() * -10);
                } else {
                    addRotation(randomFloat() * 10);
                }
                addForwardVelocity(100000 * randomFloat());
            }
        });
    }

}
