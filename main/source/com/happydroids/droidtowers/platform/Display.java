/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.badlogic.gdx.Gdx;

public class Display {
  private static float displayDensity = Float.MAX_VALUE;

  public static int scale(int pixels) {
    if (displayDensity == Float.MAX_VALUE) {
      displayDensity = Gdx.graphics.getDensity();
    }

    if (displayDensity < 1f) {
      return pixels;
    }

    return (int) ((float) pixels * displayDensity);
  }
}
