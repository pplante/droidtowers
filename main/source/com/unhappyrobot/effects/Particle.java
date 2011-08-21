package com.unhappyrobot.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static com.unhappyrobot.utils.Random.randomFloat;
import static com.unhappyrobot.utils.Random.randomInt;

public class Particle {
    private boolean alive;
    private float currentAge;
    private float totalLifespan;
    private ParticleEngine particleEngine;
    private Vector2 position;
    private Sprite sprite;
    private Vector2 velocity;
    private float scaleOverLife;
    private float currentScale;
    private float currentAlpha;
    private float alphaOverLife;

    public Particle(ParticleEngine particleEngine, Sprite sharedSprite) {
        this.particleEngine = particleEngine;
        sprite = sharedSprite;

        reset();
    }

    public boolean isAlive() {
        return alive;
    }

    public void update(float timeDelta) {
        currentAge += timeDelta;
        if(currentAge >= totalLifespan) {
            alive = false;
            return;
        }

        currentScale += scaleOverLife * timeDelta;
        currentAlpha -= alphaOverLife * timeDelta;

        position.add(velocity);
    }

    public void render(SpriteBatch batch) {
        if(!alive) return;

        sprite.setScale(currentScale);
        sprite.setPosition(position.x, position.y);
        sprite.setColor(1.0f, 1.0f, 1.0f, Math.max(0.0f, currentAlpha));
        sprite.draw(batch);
    }

    protected void reset() {
        alive = true;
        currentAge = 0.0f;
        totalLifespan = randomInt(1) + randomFloat();
        scaleOverLife = 1.5f / totalLifespan;
        alphaOverLife = 1.0f / totalLifespan;
        currentScale = 0.25f;
        currentAlpha = 0.5f;
        velocity = new Vector2(randomFloat(), randomFloat());

        position = new Vector2();
        position.set(particleEngine.getPosition());
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
