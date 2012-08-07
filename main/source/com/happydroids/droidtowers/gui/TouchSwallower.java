/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

class TouchSwallower extends Group {
  public TouchSwallower() {
    setTouchable(Touchable.enabled);
    addListener(new InputEventBlackHole());
  }
}
