/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.scenes.components.SceneManager;

public class ApplicationResumeScene extends SplashScene {
  @Override
  public void render(float deltaTime) {
    super.render(deltaTime);

    if (TowerAssetManager.assetManager().getProgress() >= 1.0f) {
      SceneManager.popScene();
    }
  }
}
