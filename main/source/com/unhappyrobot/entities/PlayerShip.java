package com.unhappyrobot.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.effects.ParticleEngine;
import com.unhappyrobot.input.Action;
import com.unhappyrobot.input.InputManager;

import java.util.ArrayList;
import java.util.List;

import static com.unhappyrobot.input.InputManager.Keys;

public class PlayerShip extends GameObject {
    public static String PLAYER_SHIP_TEXTURE = "ships/ship2.png";

    private static final float FORWARD_THRUST = 250000.0f;
    private static final float ONE_EIGHTH_IMPULSE = FORWARD_THRUST / 8;
    private static final float REVERSE_THRUST = -FORWARD_THRUST / 2;
    private static final float ROTATION_THRUST = 90.0f;
    private List<GameObject> children;
    private ParticleEngine particleEngine;

    public PlayerShip(float x, float y) {
        super(x, y);

        children = new ArrayList<GameObject>();

        particleEngine = new ParticleEngine(position, 1.0f);
        particleEngine.start();

        addChild(particleEngine);

        setMass(1000.0f);
        useTexture(PLAYER_SHIP_TEXTURE);

        loadPhysicsBodyFromJson(Gdx.files.internal("ships/ship2.json").readString());

        InputManager.bind(Keys.W, new Action() {
            public void run(float timeDelta) {
                PlayerShip.this.addForwardVelocity(FORWARD_THRUST);
            }
        });

        InputManager.bind(Keys.S, new Action() {
            public void run(float timeDelta) {
                PlayerShip.this.addForwardVelocity(REVERSE_THRUST);
            }
        });

        InputManager.bind(Keys.A, new Action() {
            public void run(float timeDelta) {
                PlayerShip.this.addRotation(ROTATION_THRUST * timeDelta);
            }
        });

        InputManager.bind(Keys.D, new Action() {
            public void run(float timeDelta) {
                PlayerShip.this.addRotation(-ROTATION_THRUST * timeDelta);
            }
        });

        InputManager.bind(Keys.R, new Action() {
            public void run(float timeDelta) {
                PlayerShip.this.reset();
            }
        });
    }

    private void addChild(GameObject gameObject) {
        children.add(gameObject);
    }

    @Override
    public void update(float timeDelta) {
        super.update(timeDelta);

        float currentVelocity = velocity.len() * timeDelta;
        if (currentVelocity >= 0.25) {
            particleEngine.setSpawnRate(currentVelocity);
        }

        for (GameObject child : children) {
            child.setPosition(reversePoint());
            child.setRotation(rotation);
            child.update(timeDelta);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        for (GameObject child : children) {
            child.render(batch);
        }
    }

    public Vector2 reversePoint() {
        Vector2 inverse = new Vector2();
        inverse.set(position);
        inverse.sub(origin.cpy().mul(0.5f));
        float radians = (float) Math.toRadians(getRotation());
        float originX = (float) (origin.x * Math.cos(radians));
        float originY = (float) (origin.y * Math.sin(radians));

        inverse.sub(originX, originY);

        return inverse;
    }
}
