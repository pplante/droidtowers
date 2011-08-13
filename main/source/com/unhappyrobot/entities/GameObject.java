package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.unhappyrobot.WorldManager;
import com.unhappyrobot.utils.Random;

public class GameObject {
    private final Vector2 position;
    private final Vector2 velocity;
    private final Vector2 direction;

    private float originX;
    private float originY;

    private float height;
    private float width;

    private float scaleX;
    private float scaleY;
    private float rotation;
    private Texture texture;
    private Sprite sprite;
    private float mass;
    private Body worldBody;
    private MassData massData;

    public GameObject(float x, float y, float scaleX, float scaleY) {
        sprite = new Sprite();
        position = new Vector2(x, y);
        direction = new Vector2(0, 0);
        velocity = new Vector2(Random.randomFloat(), Random.randomFloat());

        massData = new MassData();
        massData.mass = 100000 + Random.randomInt(100000);

        worldBody = WorldManager.addGameObject(this);
        worldBody.setLinearVelocity(velocity);
//        worldBody.setLinearDamping(Random.randomFloat());
        worldBody.setMassData(massData);

        setScale(scaleX, scaleY);
    }

    private void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        sprite.setScale(scaleX, scaleY);
    }

    public Vector2 getPosition() {
        return position;
    }

    private void setPosition(float x, float y) {
        position.set(x, y);
        sprite.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
        sprite.setOrigin(width / 2, height / 2);
    }

    public void useTexture(String filename) {
        texture = TextureDict.loadTexture(filename).get();
        sprite = new Sprite(texture);
        sprite.setSize(texture.getWidth(), texture.getHeight());
    }

    public void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public void update(float timeDelta) {
        if(Random.randomFloat() <= 0.5) {
            worldBody.applyForce(velocity, new Vector2(Random.randomFloat(), Random.randomFloat()));
//            worldBody.applyAngularImpulse(Random.randomFloat() * Random.randomInt(10));
            worldBody.setFixedRotation(true);
        }

        position.set(worldBody.getPosition());
        sprite.setPosition(position.x, position.y);
    }
}
