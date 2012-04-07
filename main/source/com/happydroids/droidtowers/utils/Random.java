/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

public class Random {
  private Random() {
  }

  public final static java.util.Random random = new java.util.Random(System.nanoTime());

  public static int randomInt(int maxVal) {
    return random.nextInt(maxVal);
  }

  public static float randomFloat() {
    return random.nextFloat();
  }

  public static int randomInt(int min, int max) {
    return min + random.nextInt(max - min);
  }

  public static int randomInt(double min, double max) {
    return randomInt((int) min, (int) max);
  }
}
