/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;

public class WheelScrollFlickScrollPane extends FlickScrollPane {
  @Override
  public boolean touchMoved(float x, float y) {
    if (super.hit(x, y) != null) {
      getStage().setScrollFocus(this);
    } else {
      getStage().setScrollFocus(null);
    }

    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    float scrollY = getScrollY() + (amount * 10);

    scrollY = Math.min(scrollY, getMaxY());
    scrollY = Math.max(scrollY, 0);

    setScrollY(scrollY);

    return true;
  }
}
