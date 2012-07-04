/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.utils.Random;

import java.util.Iterator;

public class ParticleEffectManager extends GameObject {
  private final ParticleEffect activeEffect;
  private final Iterator<float[]> colorsIterator;
  private final Rectangle worldBounds;
  private float playbackDuration;
  private float playbackTime;
  private boolean stopped;

  public ParticleEffectManager(ParticleEffect activeEffect, Iterator<float[]> colorsIterator, Rectangle worldBounds) {
    super();

    this.activeEffect = activeEffect;
    this.colorsIterator = colorsIterator;
    this.worldBounds = worldBounds;
    stopped = true;
  }

  public void resetEffect() {
    stopped = false;
    playbackTime = 0f;
    playbackDuration = 1f + Random.randomInt(1.75f, 4.5f);

    activeEffect.setPosition(Random.randomInt(worldBounds.x, worldBounds.width + 1), Random.randomInt(worldBounds.y, worldBounds.height + 1));

    for (ParticleEmitter emitter : activeEffect.getEmitters()) {
      emitter.reset();

      if (!emitter.getName().contains("smoke")) {
        emitter.getTint().setColors(colorsIterator.next());
      }
    }

  }

  @Override
  public void update(float timeDelta) {
    playbackTime += timeDelta;

    if (!stopped && playbackDuration <= playbackTime) {
      resetEffect();
    }

    if (!activeEffect.isComplete()) {
      activeEffect.update(timeDelta);
    }
  }

  @Override
  public void draw(SpriteBatch spriteBatch) {
    if (!activeEffect.isComplete()) {
      activeEffect.draw(spriteBatch);
    }
  }

  public void stop() {
    stopped = true;
  }
}
