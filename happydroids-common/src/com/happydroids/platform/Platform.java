/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import java.io.File;

public class Platform {
  private Platform() {

  }

  public static Platforms getOSType() {
    String os = System.getProperty("os.name").toLowerCase();

    if (os.contains("mac")) {
      return Platforms.Mac;
    } else if (os.contains("win")) {
      return Platforms.Windows;
    } else if (os.contains("nix") || os.contains("nux") || os.contains("sun")) {
      return Platforms.Unix;
    }

    return Platforms.Unknown;
  }

  public static File getAppRoot() {
    String userHome = System.getProperty("user.home");
    String appName = "DroidTowers";
    String path = null;
    switch (getOSType()) {
      case Mac:
        path = String.format("%s/Library/Application Support/%s", userHome, appName);
        break;
      case Unix:
        path = String.format("%s/.%s", userHome, appName);
        break;
      case Windows:
        String appDataPath = System.getenv("APPDATA");
        if(appDataPath != null) {
          path = String.format("%s/.%s", appDataPath, appName);
        } else {
          path = String.format("%s/.%s", userHome, appName);
        }
        break;
    }

    File workingDir = new File(path);
    if(!workingDir.exists()) {
      boolean madeDir = workingDir.mkdir();
      if(!madeDir) {
        throw new RuntimeException("Could not create the required local storage.");
      }
    }

    return workingDir;
  }
}
