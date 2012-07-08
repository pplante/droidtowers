/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;


import java.text.NumberFormat;

public class StringUtils {
  public static CharSequence formatNumber(long i) {
    return NumberFormat.getInstance().format(i);
  }

  public static CharSequence formatNumber(double i) {
    return NumberFormat.getInstance().format(i);
  }
}
