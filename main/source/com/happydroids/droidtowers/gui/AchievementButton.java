/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.achievements.AchievementEngine;

public class AchievementButton extends ImageButton {

  public static final float ANIMATION_DURATION = 0.125f;
  public static final int ANIMATION_DELAY = 15000;
  private final Animation activeAnimation;
  private float animationTime;
  private long nextAnimationTime;
  private boolean waitToAnimate;
  private final ParticleEffect particleEffect;

  public AchievementButton(AchievementEngine achievementEngine) {
    super(TowerAssetManager.textureFromAtlas("achievements", "hud/buttons.txt"));

    activeAnimation = TowerAssetManager.animationFromAtlas("achievements-active", "hud/buttons.txt", ANIMATION_DURATION);
    nextAnimationTime = 0;

    particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("particles/sparkle.p"), Gdx.files.internal("particles"));

    setClickListener(new VibrateClickListener() {
      public void onClick(Actor actor, float x, float y) {
        new AchievementListView(getStage()).show();
      }
    });

    visible = false;
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
      batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
      batch.draw(activeAnimation.getKeyFrame(animationTime, false), x, y, width, height);
    } else {
      super.draw(batch, parentAlpha);
    }
  }

  public ParticleEffect getParticleEffect() {
    return particleEffect;
  }
}
