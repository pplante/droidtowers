/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

public class Strings {
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
}
