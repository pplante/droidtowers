/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.platform.Platform;

import java.net.URI;

public class ApplicationResumeScene extends SplashScene {
  @Override
  public void render(float deltaTime) {
    super.render(deltaTime);

    if (TowerAssetManager.assetManager().getProgress() >= 1.0f) {
      SceneManager.popScene();

      if (Platform.protocolHandler != null && Platform.protocolHandler.hasUri()) {
        URI launchUri = Platform.protocolHandler.consumeUri();
        SceneManager.pushScene(LaunchUriScene.class, launchUri);
      }
    }
  }
}
