/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.Colors;
import com.happydroids.droidtowers.achievements.AchievementEngine;

public class AchievementButton extends ColorizedImageButton {

  public static final float ANIMATION_DURATION = 0.125f;
  public static final int ANIMATION_DELAY = 15000;
  private final Animation activeAnimation;
  private float animationTime;
  private long nextAnimationTime;
  private boolean waitToAnimate;
  private final ParticleEffect particleEffect;

  public AchievementButton(TextureAtlas hudAtlas, AchievementEngine achievementEngine) {
    super(hudAtlas.findRegion("achievements"), Colors.ICS_BLUE);

    activeAnimation = new Animation(ANIMATION_DURATION, hudAtlas.findRegions("achievements-active"));
    nextAnimationTime = 0;

    particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("particles/sparkle.p"), Gdx.files.internal("particles"));

    addListener(new VibrateClickListener() {
      public void onClick(InputEvent event, float x, float y) {
        new AchievementListView(getStage()).show();
      }
    });

    setVisible(false);
  }

  @Override
  public void act(float delta) {
    if (AchievementEngine.instance().hasPendingAwards()) {
      if (waitToAnimate) {
        if (nextAnimationTime <= System.currentTimeMillis()) {
          animationTime = 0f;
          waitToAnimate = false;
        }
      } else if (activeAnimation.isAnimationFinished(animationTime)) {
        waitToAnimate = true;
        nextAnimationTime = System.currentTimeMillis() + ANIMATION_DELAY;
      }

      animationTime += delta;
      particleEffect.update(delta);
    }
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    if (AchievementEngine.instance().hasPendingAwards()) {
      particleEffect.draw(batch);
      batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
      batch.draw(activeAnimation.getKeyFrame(animationTime, false), getX(), getY(), getWidth(), getHeight());
    } else {
      super.draw(batch, parentAlpha);
    }
  }

  public ParticleEffect getParticleEffect() {
    return particleEffect;
  }
}
