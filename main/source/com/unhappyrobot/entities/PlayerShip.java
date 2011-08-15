package com.unhappyrobot.entities;

import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.utils.Random;

public class PlayerShip extends GameObject {
    public static String PLAYER_SHIP_TEXTURE = "ships/scout1.png";

    public PlayerShip(float x, float y) {
        super(x, y);
        setLinearVelocity(new Vector2(Random.randomFloat(), Random.randomFloat()));
        useTexture(PLAYER_SHIP_TEXTURE);
    }
}
