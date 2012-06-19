/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.List;

public class AnimatedImage extends Image {
  private final Animation animation;
  private float playbackTime;
  private float playbackDelay;
  private boolean hasPlaybackDelay;
  private final boolean shouldLoop;

  public AnimatedImage(List<TextureAtlas.AtlasRegion> frames, float frameDuration, boolean shouldLoop) {
    super(frames.get(0));
    this.shouldLoop = shouldLoop;

    animation = new Animation(frameDuration, frames);
    playbackTime = 0f;
  }

  @Override
  public void act(float delta) {
    super.act(delta);

    playbackTime += delta;
    if (hasPlaybackDelay) {
      if (playbackTime > playbackDelay) {
        playbackTime = 0f;
      } else if (playbackTime >= animation.animationDuration) {
        return;
      }
    }

    setRegion(animation.getKeyFrame(playbackTime, shouldLoop));
  }

  public void delayAfterPlayback(float delay) {
    hasPlaybackDelay = true;
    playbackDelay = delay;
  }
}
