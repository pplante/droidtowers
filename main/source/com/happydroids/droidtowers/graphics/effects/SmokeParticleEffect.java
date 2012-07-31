/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.graphics.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.happydroids.droidtowers.entities.GameObject;

public class SmokeParticleEffect extends GameObject {
  private final ParticleEffect particleEffect;

  public SmokeParticleEffect() {
    particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("particles/smoke.p"), Gdx.files.internal("particles"));
  }

  @Override
  public void setPosition(float x, float y) {
    particleEffect.setPosition(x, y);
  }

  @Override
  public void setSize(float width, float height) {
    for (ParticleEmitter emitter : particleEffect.getEmitters()) {
      emitter.getSpawnWidth().setHigh(width);
      emitter.getSpawnHeight().setHigh(height);
    }
  }

  @Override
  public void draw(SpriteBatch spriteBatch) {
    particleEffect.draw(spriteBatch);
  }

  @Override
  public void update(float timeDelta) {
    particleEffect.update(timeDelta);

    if (particleEffect.isComplete()) {
      markToRemove(true);
    }
  }

  public void start() {
    particleEffect.start();
  }

  @Override
  public boolean shouldBeCulled() {
    return false;
  }
}
