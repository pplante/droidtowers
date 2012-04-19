/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

public class Display {
  private static float scaledDensity = 1f;

  public static int scale(int pixels) {
    return (int) ((float) pixels * scaledDensity);
  }

  public static void setScaledDensity(float scaledDensity) {
    Display.scaledDensity = scaledDensity;
  }

  public static float getScaledDensity() {
    return scaledDensity;
  }
}
