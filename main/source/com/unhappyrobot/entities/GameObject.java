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
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 origin;
    private Vector2 size;
    private Vector2 scale;

    private Sprite sprite;
    protected Body worldBody;
    private float angle;
    private PhysicsShapes physicsShape;
    protected float radius;

    public GameObject(float x, float y, float scaleX, float scaleY) {
        sprite = new Sprite();
        position = new Vector2();
        origin = new Vector2();
        size = new Vector2();
        scale = new Vector2();
        velocity = new Vector2();
        physicsShape = PhysicsShapes.POLYGON;

        setPosition(x, y);
        setScale(scaleX, scaleY);
    }

    public GameObject(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    public void setupPhysics() {
        MassData massData = new MassData();
        massData.mass = 100000 + Random.randomInt(100000);

        worldBody = WorldManager.addGameObject(this);
        worldBody.setLinearVelocity(velocity);
        worldBody.setMassData(massData);
    }

    public void setLinearVelocity(Vector2 vel) {
        velocity.set(vel);
        if (worldBody != null)
            worldBody.setLinearVelocity(velocity);
    }

    public void setScale(float scaleX, float scaleY) {
        scale.set(scaleX, scaleY);
        sprite.setScale(scale.x, scale.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    private void setPosition(float x, float y) {
        position.set(x, y);
        sprite.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        size.set(width, height);
        origin.set(width / 2, height / 2);

        sprite.setOrigin(origin.x, origin.y);
        sprite.setSize(size.x, size.y);
    }

    public Vector2 getSize() {
        return size;
    }

    public void useTexture(String filename) {
        Texture texture = TextureDict.loadTexture(filename).get();

        sprite.setTexture(texture);
        sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());

        setSize(texture.getWidth(), texture.getHeight());
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void update(float timeDelta) {
        position.add(velocity);
        sprite.translate(velocity.x, velocity.y);
    }

    public void beforePhysicsUpdate() {
        worldBody.setTransform(position, angle);
    }

    public void afterPhysicsUpdate() {
        angle = worldBody.getAngle();
        velocity.set(worldBody.getLinearVelocity());
        position.set(worldBody.getPosition());
        position.rotate(angle);
    }

    public PhysicsShapes getPhysicsShape() {
        return physicsShape;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;

        setScale(radius, radius);
    }

    public enum PhysicsShapes {
        POLYGON,
        CIRCLE
    }
}
