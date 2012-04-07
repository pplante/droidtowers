/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky.platform;

import com.happydroids.utils.OSValidator;

public class PlatformProtocolHandlerFactory {
  public static PlatformProtocolHandler newInstance() {
    if(OSValidator.getOSType().equals("macosx")) {
      return new MacProtocolHandler();
    } else if(OSValidator.getOSType().equals("windows")) {
      return new WindowsProtocolHandler();
    }
    return null;
  }
}
