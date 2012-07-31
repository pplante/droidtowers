/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.math;

public class StatLog {
  private float min;
  private float max;
  private float total;
  private float[] values;
  private int idx;

  public void reset(int size) {
    min = Float.MAX_VALUE;
    max = Float.MIN_VALUE;
    total = 0;
    idx = 0;
    values = new float[size];
  }

  public void record(float value) {
    min = Math.min(value, min);
    max = Math.max(value, min);
    total += value;
    values[idx++] = value;

    if (idx == values.length) {
      total -= values[0];
      idx = 0;
    }
  }

  public float normalize(float value) {
    return (value - min) / getRange();
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }

  public float getRange() {
    return max - min;
  }

  public float getAverage() {
    if (total > 0f) {
      return total / values.length;
    }

    return 0;
  }

  public float standardDeviation() {
    float average = getAverage();

    float seriesSquared = 0;
    for (float v : values) {
      seriesSquared += Math.exp(v - average);
    }

    return (float) Math.sqrt(seriesSquared / values.length);
  }
}
