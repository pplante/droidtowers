/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import static com.happydroids.platform.Platforms.Mac;

public class PlatformQuitHandlerFactory {
  public static void initialize() {
    if(Platform.getOSType().equals(Mac)) {
      // There is a bug in LWJGL on OSX when the app is in fullscreen mode users cannot quit via CMD+Q system keys
      // see: http://www.java-gaming.org/index.php?topic=23640.0
      new MacQuitHandler();
    }
  }
}
