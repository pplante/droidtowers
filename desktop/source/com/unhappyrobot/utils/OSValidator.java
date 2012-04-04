/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.utils;

public class OSValidator {
  private OSValidator() {

  }

  public static String getOSType() {
    String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("mac")) {
      return "macosx";
    } else if (os.contains("win")) {
      return "windows";
    } else if (os.contains("nix") || os.contains("nux") || os.contains("sun")) {
      return "linux";
    }

    return "unknown";
  }
}
