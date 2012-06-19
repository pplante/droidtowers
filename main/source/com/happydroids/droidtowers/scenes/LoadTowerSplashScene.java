/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.scenes.components.SceneManager;

import static com.happydroids.droidtowers.TowerAssetManager.assetManager;

public class LoadTowerSplashScene extends SplashScene {
  private GameSave gameSave;

  @Override
  public void create(Object... args) {
    super.create(args);

    if (args != null) {
      gameSave = ((GameSave) args[0]);
    }
  }

  @Override
  public void render(float deltaTime) {
    super.render(deltaTime);

    if (assetManager().getProgress() >= 1.0f) {
      SceneManager.changeScene(TowerScene.class, gameSave);
    }
  }
}
