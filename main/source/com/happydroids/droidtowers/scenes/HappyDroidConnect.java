/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.scenes;

import com.happydroids.droidtowers.gui.ConnectToHappyDroidsWindow;
import com.happydroids.droidtowers.scenes.components.SceneManager;

public class HappyDroidConnect extends Scene {
  private ConnectToHappyDroidsWindow connectWindow;

  @Override
  public void create(Object... args) {
    Runnable postConnectRunnable = null;
    if (args != null && args.length > 0) {
      postConnectRunnable = (Runnable) args[0];
    }
    connectWindow = new ConnectToHappyDroidsWindow(getStage());
    connectWindow.setDismissCallback(new Runnable() {
      public void run() {
        if (SceneManager.previousScene() instanceof MainMenuScene) {
          SceneManager.popScene();
        } else {
          SceneManager.changeScene(MainMenuScene.class);
        }
      }
    });
    connectWindow.setPostConnectRunnable(postConnectRunnable);
    connectWindow.show();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void render(float deltaTime) {
  }

  @Override
  public void dispose() {
  }
}
