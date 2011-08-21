package com.unhappyrobot.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.TextureDict;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticleEngine extends GameObject {
    public static final String DEFAULT_TEXTURE = "particle.png";
    private int maxParticles;
    private int minActive;
    private List<Particle> activeParticles;
    private List<Particle> inactiveParticles;
    private static HashMap<String, Sprite> sprites;
    private static Sprite defaultSprite;
    private Vector2 position;
    private boolean allowedToSpawn;
    private float spawnRate;

    public ParticleEngine(float x, float y, float scale) {
        super(x, y, scale);

        if (defaultSprite == null) {
            defaultSprite = makeSprite(DEFAULT_TEXTURE);
        }

        setMaxParticles(100);
        setMinActive(10);
        System.out.println("hello?");
    }

    public ParticleEngine(Vector2 position, float scale) {
        this(position.x, position.y, scale);
    }

    private Sprite makeSprite(String spriteTexture) {
        if (sprites == null) {
            sprites = new HashMap<String, Sprite>();
        }

        Sprite sprite = new Sprite(TextureDict.loadTexture(spriteTexture).get());

        sprites.put(spriteTexture, sprite);

        return sprite;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;

        activeParticles = new ArrayList<Particle>(maxParticles);
        inactiveParticles = new ArrayList<Particle>(maxParticles);

        Sprite defaultSprite = sprites.get(DEFAULT_TEXTURE);

        for (int i = 0; i < this.maxParticles; i++) {
            inactiveParticles.add(new Particle(this, defaultSprite));
        }
    }

    @Override
    public void update(float timeDelta) {
        for (Particle activeParticle : activeParticles) {
            activeParticle.update(timeDelta);
        }

        List<Particle> toBeRemoved = new ArrayList<Particle>(activeParticles.size());
        for (Particle particle : activeParticles) {
            if (!particle.isAlive()) {
                toBeRemoved.add(particle);
            }
        }

        activeParticles.removeAll(toBeRemoved);
        inactiveParticles.addAll(toBeRemoved);


        if (allowedToSpawn) {
            int needToSpawn = Random.randomInt(Math.min(5, inactiveParticles.size()));
            if (activeParticles.size() < minActive) {
                needToSpawn += minActive - activeParticles.size();
            }

            needToSpawn *= spawnRate;

            for (int i = 0; i < Math.min(needToSpawn, inactiveParticles.size()); i++) {
                Particle particle = inactiveParticles.get(i);
                inactiveParticles.remove(i);
                activeParticles.add(particle);

                particle.reset();
            }
        }

        System.out.printf("active: %d, inactive: %d\n", activeParticles.size(), inactiveParticles.size());
    }

    public void render(SpriteBatch batch) {
        for (Particle activeParticle : activeParticles) {
            activeParticle.render(batch);
        }

        defaultSprite.setPosition(getPosition().x, getPosition().y);
        defaultSprite.setScale(0.5f);
        defaultSprite.setColor(Color.RED);
//        defaultSprite.setOrigin(origin.x, origin.y);
        defaultSprite.draw(batch);
    }

    public void setMinActive(int minActive) {
        this.minActive = minActive;
    }

    public int getMinActive() {
        return minActive;
    }

    public void start() {
        this.allowedToSpawn = true;
    }

    public void stop() {
        this.allowedToSpawn = false;
    }

    public void setSpawnRate(float spawnRate) {
        this.spawnRate = spawnRate;
    }
}
