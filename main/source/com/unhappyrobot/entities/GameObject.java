package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.unhappyrobot.WorldManager;
import com.unhappyrobot.physics.PhysicsBody;

import java.io.IOException;

public class GameObject {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Vector2 origin;
    protected Vector2 size;
    protected float scale;
    protected float angle;
    protected float radius;

    protected Sprite sprite;
    protected Body worldBody;
    protected PhysicsShapes physicsShape;
    protected float rotation;
    private PhysicsBody physicsBody;
    protected Sprite originPointSprite;
    private float worldMass;

    public GameObject(float x, float y, float scale) {
        sprite = new Sprite();
        position = new Vector2();
        origin = new Vector2();
        size = new Vector2();
        velocity = new Vector2();
        physicsShape = PhysicsShapes.POLYGON;
        worldMass = 0.0f;

        originPointSprite = new Sprite(TextureDict.loadTexture("contact-point.png").get());

        setPosition(x, y);
        setScale(scale);
        setLinearDamping(0.1f);
    }

    public GameObject(float x, float y) {
        this(x, y, 1.0f);
    }

    public GameObject(Vector2 position, float scale) {
        this(position.x, position.y, scale);
    }

    public void setupPhysics() {
        worldBody = WorldManager.addGameObject(this, physicsBody);

        setMass(worldMass);
    }

    public boolean loadPhysicsBodyFromJson(String jsonData) {
        try {
            physicsBody = PhysicsBody.fromJson(jsonData);
            physicsShape = PhysicsShapes.POLYGON;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setScale(float scale) {
        this.scale = scale;
        sprite.setScale(this.scale, this.scale);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        setPosition(new Vector2(x, y));
    }

    public void setPosition(Vector2 vec) {
        position.set(vec);

        if (worldBody != null)
            worldBody.setTransform(position, (float) Math.toRadians(rotation));
    }

    public void setSize(float width, float height) {
        size.set(width, height);
        origin.set(size);
        origin.mul(0.5f);

        sprite.setOrigin(origin.x, origin.y);
        sprite.setSize(size.x, size.y);
//        sprite.setScale(scale.x, scale.y);
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
        originPointSprite.draw(batch);
    }

    public void update(float timeDelta) {
        position.set(worldBody.getWorldCenter());
        rotation = (float) Math.toDegrees(worldBody.getAngle());
        velocity.set(worldBody.getLinearVelocity());

        sprite.setPosition(position.x - origin.x, position.y - origin.y);
        sprite.setRotation(rotation);
        originPointSprite.setPosition(position.x, position.y);
    }

    public PhysicsShapes getPhysicsShape() {
        return physicsShape;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;

        setScale(radius);
    }

    protected void setLinearDamping(float v) {
    }

    public Vector2 getLinearVelocity() {
        return velocity;
    }

    public void addForwardVelocity(float forwardThrust) {
        float radians = (float) Math.toRadians(getRotation());

        float forwardThrustX = (float) (forwardThrust * Math.cos(radians));
        float forwardThrustY = (float) (forwardThrust * Math.sin(radians));
        worldBody.applyForce(new Vector2(forwardThrustX, forwardThrustY), worldBody.getWorldCenter());
    }

    protected void addRotation(float rotationDelta) {
        this.rotation += rotationDelta;

        if (this.rotation > 360.0f) {
            this.rotation = 0.0f;
        } else if (this.rotation < 0.0f) {
            this.rotation = 360.0f;
        }

        worldBody.setTransform(position, (float) Math.toRadians(rotation));
        position = worldBody.getPosition();
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        this.sprite.setRotation(this.rotation);
        if (worldBody != null) {
            worldBody.setTransform(position, (float) Math.toRadians(rotation));
            position = worldBody.getPosition();
        }
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public void setMass(float v) {
        worldMass = v;

        if (worldBody != null) {
            MassData massData = new MassData();
            massData.mass = worldMass;

            worldBody.setMassData(massData);
        }
    }

    protected void reset() {
        setRotation(0.0f);
        worldBody.setLinearVelocity(0.0f, 0.0f);
    }

    public float getScale() {
        return scale;
    }

    public void update(GameObject parent, float timeDelta) {

    }

    public enum PhysicsShapes {
        POLYGON,
        CIRCLE
    }
}
