/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gui.DebugWindow;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.gui.TowerWindow;
import com.happydroids.droidtowers.scenes.TowerScene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.utils.Screenshot;

public class DebugInputAdapter extends InputAdapter {
  public StringBuilder keys = new StringBuilder();
  private TowerWindow debugWindow;

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case InputSystem.Keys.F1:
        TowerAssetManager.assetManager().invalidateAllTextures();
        return true;

      case Input.Keys.F2:
        SceneManager.restartActiveScene();
        return true;

      case Input.Keys.F6:
        Screenshot.capture();
        return true;

      case Input.Keys.ALT_RIGHT:
      case Input.Keys.MENU:
        if (debugWindow != null) {
          debugWindow.dismiss();
          debugWindow = null;
        } else {
          debugWindow = new DebugWindow(DroidTowersGame.getRootUiStage()).show();
          debugWindow.setDismissCallback(new Runnable() {
            @Override
            public void run() {
              debugWindow = null;
            }
          });
        }
        return true;
    }

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    keys.append(character);

    if (SceneManager.activeScene() instanceof TowerScene) {
      if (keys.toString().endsWith("ggmoney")) {
        Player.instance().addCurrency(100000);
        HeadsUpDisplay.showToast("Money!!!!");
      }
    }

    if (keys.length() == 20) {
      keys.deleteCharAt(0);
    }

    return false;
  }
}
