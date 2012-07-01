/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.badlogic.gdx.Gdx;

public class Display {
  private static float scaledDensity = 1f;
  private static boolean xHdpiMode;
  private static int biggestScreenDimension = -1;

  public static int scale(float pixels) {
    return scale((int) pixels);
  }

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

  public static void setXHDPI(boolean hdpi) {
    xHdpiMode = hdpi;
  }

  public static boolean isXHDPIMode() {
    return xHdpiMode;
  }

  public static int getBiggestScreenDimension() {
    if (biggestScreenDimension == -1) {
      biggestScreenDimension = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 2;
    }

    return biggestScreenDimension;
  }
}
