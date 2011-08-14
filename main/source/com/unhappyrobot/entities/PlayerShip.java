package com.unhappyrobot.entities;

public class PlayerShip extends GameObject {
    public static String PLAYER_SHIP_TEXTURE = "ships/scout1.png";

    public PlayerShip(float x, float y) {
        super(x, y);

        useTexture(PLAYER_SHIP_TEXTURE);
    }
}
