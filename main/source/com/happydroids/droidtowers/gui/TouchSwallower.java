/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;

class TouchSwallower extends Group {
  public TouchSwallower() {
    setTouchable(Touchable.enabled);
    addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        return true;
      }
    });
  }
}
