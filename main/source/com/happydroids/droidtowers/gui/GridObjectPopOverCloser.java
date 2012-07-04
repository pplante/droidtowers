/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

class GridObjectPopOverCloser extends InputAdapter {
  private HeadsUpDisplay headsUpDisplay;

  public GridObjectPopOverCloser(HeadsUpDisplay headsUpDisplay) {
    this.headsUpDisplay = headsUpDisplay;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    if (headsUpDisplay.getGridObjectPopOver() != null && headsUpDisplay.getGridObjectPopOver().visible) {
      headsUpDisplay.setGridObjectPopOver(null);

      return true;
    }

    return false;
  }

  @Override
  public boolean keyDown(int keycode) {
    if (headsUpDisplay.getGridObjectPopOver() != null) {
      if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
        headsUpDisplay.setGridObjectPopOver(null);

        return true;
      }
    }

    return false;
  }
}
