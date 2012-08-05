/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class VibrateClickListener extends ClickListener {
  public abstract void onClick(InputEvent event, float x, float y);

  @Override
  public void clicked(InputEvent event, float x, float y) {
    Gdx.input.vibrate(15);
    onClick(event, x, y);
  }
}
