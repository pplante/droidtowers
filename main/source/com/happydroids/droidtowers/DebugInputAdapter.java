/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.happydroids.droidtowers.entities.Player;
import com.happydroids.droidtowers.gui.HeadsUpDisplay;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.scenes.TowerScene;

class DebugInputAdapter extends InputAdapter {
  public StringBuilder keys = new StringBuilder();

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case InputSystem.Keys.F1:
        Texture.invalidateAllTextures(Gdx.app);
        TowerAssetManager.assetManager().finishLoading();
        return true;
    }

    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    keys.append(character);

    if (TowerGame.getActiveScene() instanceof TowerScene) {
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
