/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

class InputEventBlackHole implements EventListener {
  @Override
  public boolean handle(Event e) {
    if (!(e instanceof InputEvent)) {
      return false;
    }
    InputEvent event = (InputEvent) e;
    event.cancel();
    return true;
  }
}
