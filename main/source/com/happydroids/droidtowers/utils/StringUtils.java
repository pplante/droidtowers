/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;


import com.happydroids.droidtowers.TowerConsts;

import java.text.NumberFormat;

public class StringUtils {
  public static CharSequence formatNumber(long i) {
    return NumberFormat.getInstance().format(i);
  }

  public static CharSequence formatNumber(double i) {
    return NumberFormat.getInstance().format(i);
  }

  public static String wrap(String text, int wrapAt) {
    if (text.length() <= wrapAt) {
      return text;
    }

    StringBuilder output = new StringBuilder();
    int lineBegin = 0;
    int lineEnd = text.indexOf(" ", Math.min(wrapAt, text.length()));
    if (lineEnd != -1) {
      do {
        output.append(text.substring(lineBegin, lineEnd));
        output.append("\n");
        lineBegin = lineEnd + 1;
        lineEnd = text.indexOf(' ', Math.min(lineBegin + wrapAt, text.length()));
      } while (lineBegin < text.length() && lineEnd != -1);
    }

    output.append(text.substring(lineBegin));
    output.append("\n");

    return output.toString().trim();
  }

  public static String truncate(String title, int finalSize) {
    if (title.length() <= finalSize) {
      return title;
    }

    return title.substring(0, finalSize - 3) + "...";
  }

  public static String currencyFormat(int value) {
    return TowerConsts.CURRENCY_SYMBOL + NumberFormat.getInstance().format(value);
  }

  public static CharSequence join(Iterable<String> strings, final String separator) {
    return org.apache.commons.lang3.StringUtils.join(strings, separator);
  }
}
