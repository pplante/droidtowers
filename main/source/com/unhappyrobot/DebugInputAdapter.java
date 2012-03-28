package com.unhappyrobot;

import com.badlogic.gdx.InputAdapter;
import com.unhappyrobot.entities.Player;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.scenes.TowerScene;

class DebugInputAdapter extends InputAdapter {
  public StringBuilder keys = new StringBuilder();

  @Override
  public boolean keyTyped(char character) {
    keys.append(character);

    if (TowerGame.getActiveScene() instanceof TowerScene) {
      if (keys.toString().endsWith("ggmoney")) {
        Player.instance().addCurrency(100000);
        HeadsUpDisplay.instance().showToast("Money!!!!");
      }
    }

    if (keys.length() == 20) {
      keys.deleteCharAt(0);
    }

    return false;
  }
}
