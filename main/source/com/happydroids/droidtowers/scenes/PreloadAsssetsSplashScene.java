/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.scenes.components.SceneManager;

public class PreloadAsssetsSplashScene extends SplashScene {

  private Runnable postPreloadRunnable;

  @Override
  public void create(Object... args) {
    super.create(args);

    if (args != null && args.length > 0) {
      postPreloadRunnable = (Runnable) args[0];
    }
  }

  @Override
  public void render(float deltaTime) {
    super.render(deltaTime);

    if (TowerAssetManager.preloadFinished()) {
      if (postPreloadRunnable != null) {
        postPreloadRunnable.run();
      } else {
        SceneManager.popScene();
      }
    }
  }
}
