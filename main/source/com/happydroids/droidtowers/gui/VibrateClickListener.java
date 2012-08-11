/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class VibrateClickListener extends ClickListener {
  private static boolean vibrateEnabled;

  public static void setVibrateEnabled(boolean vibrateEnabled) {
    VibrateClickListener.vibrateEnabled = vibrateEnabled;
  }

  public static boolean isVibrateEnabled() {
    return vibrateEnabled;
  }

  public abstract void onClick(InputEvent event, float x, float y);

  @Override
  public void clicked(InputEvent event, float x, float y) {
    if (vibrateEnabled) {
      Gdx.input.vibrate(15);
    }
    onClick(event, x, y);
  }
}
