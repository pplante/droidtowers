/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky.platform;

import com.happydroids.platform.Platform;
import com.happydroids.platform.Platforms;

public class PlatformProtocolHandlerFactory {
  public static PlatformProtocolHandler newInstance() {
    if (Platform.getOSType().equals(Platforms.Mac)) {
      return new MacProtocolHandler();
    } else if (Platform.getOSType().equals(Platforms.Windows)) {
      return new WindowsProtocolHandler();
    }
    return null;
  }
}
