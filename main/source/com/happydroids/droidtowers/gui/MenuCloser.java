/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class MenuCloser extends InputAdapter {
  private Menu menu;

  public MenuCloser(Menu menu) {
    this.menu = menu;
  }

  public boolean touchDown(int x, int y, int pointer, int button) {
    Vector2 touchPoint = new Vector2();
    menu.getStage().toStageCoordinates(x, y, touchPoint);
    menu.toLocalCoordinates(touchPoint);
    if (menu.hit(touchPoint.x, touchPoint.y) == null) {
      menu.close();
      return true;
    }

    return false;
  }
}
