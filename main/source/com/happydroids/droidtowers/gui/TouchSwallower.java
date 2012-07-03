/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Group;

class TouchSwallower extends Group {
  @Override
  public boolean touchDown(float x, float y, int pointer) {
    return true;
  }
}
