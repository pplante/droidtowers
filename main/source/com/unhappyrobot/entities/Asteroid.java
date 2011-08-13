package com.unhappyrobot.entities;

import static com.unhappyrobot.utils.Random.*;

public class Asteroid extends GameObject {
    public static String[] ASTEROID_TEXTURES = {
        "asteroids/big-01.png",
//        "asteroids/mid-01.png",
        "asteroids/small-01.png",
    };

    public Asteroid(float x, float y, float scaleX, float scaleY) {
        super(x, y, scaleX, scaleY);

        useTexture(ASTEROID_TEXTURES[randomInt(ASTEROID_TEXTURES.length)]);
    }


}
