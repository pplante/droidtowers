/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.badlogic.gdx.Gdx;

public class Display {
  private static float scaledDensity = 1f;
  private static boolean xHdpiMode;
  private static int biggestScreenDimension = -1;
  private static int actualWidth;
  private static int actualHeight;
  private static int scaledWidth;
  private static int scaledHeight;

  public static void setup() {
    actualWidth = Gdx.graphics.getWidth();
    actualHeight = Gdx.graphics.getHeight();
    scaledWidth = actualWidth;
    scaledHeight = actualHeight;

    if (actualWidth < 800) {
      scaledWidth = 800;
      scaledHeight = 480;
    }
  }

  public static int scale(float pixels) {
    return devicePixel((int) pixels);
  }

  public static int devicePixel(int pixels) {
    return (int) ((float) pixels * scaledDensity);
  }

  public static void setScaledDensity(float scaledDensity) {
    Display.scaledDensity = scaledDensity;
  }

  public static float getScaledDensity() {
    return scaledDensity;
  }


  public static int percentOfScreen(float percent) {
    return (int) (Display.getWidth() * percent);
  }

  public static void setXHDPI(boolean hdpi) {
    xHdpiMode = hdpi;
  }

  public static boolean isXHDPIMode() {
    return xHdpiMode;
  }

  public static int getBiggestScreenDimension() {
    if (biggestScreenDimension == -1) {
      biggestScreenDimension = Math.max(Display.getWidth(), Display.getHeight()) / 2;
    }

    return biggestScreenDimension;
  }

  public static int getWidth() {
//    return Gdx.graphics.getWidth();
    return scaledWidth;
  }

  public static int getHeight() {
//    return Gdx.graphics.getHeight();
    return scaledHeight;
  }

  public static boolean isInCompatibilityMode() {
    return scaledWidth < actualWidth;
  }
}
