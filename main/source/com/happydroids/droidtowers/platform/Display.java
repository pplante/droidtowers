/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.badlogic.gdx.Gdx;

public class Display {
  private static float scaledDensity = 1f;
  private static boolean hdpiMode;

  public static int scale(int pixels) {
    return (int) ((float) pixels * scaledDensity);
  }

  public static void setScaledDensity(float scaledDensity) {
    Display.scaledDensity = scaledDensity;
  }

  public static float getScaledDensity() {
    return scaledDensity;
  }


  public static int percentOfScreen(float percent) {
    return (int) (Gdx.graphics.getWidth() * percent);
  }

  public static void setHDPI(boolean hdpi) {
    hdpiMode = hdpi;
  }

  public static boolean isHDPIMode() {
    return hdpiMode;
  }
}
