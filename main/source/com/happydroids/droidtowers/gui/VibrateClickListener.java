/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;

public abstract class VibrateClickListener implements ClickListener {
  public void click(Actor actor, float x, float y) {
    Gdx.input.vibrate(15);
    onClick(actor, x, y);
  }

  public abstract void onClick(Actor actor, float x, float y);
}
