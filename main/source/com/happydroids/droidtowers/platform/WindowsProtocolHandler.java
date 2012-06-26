/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.happydroids.platform.PlatformProtocolHandler;

import java.net.URI;

public class WindowsProtocolHandler implements PlatformProtocolHandler {
  public void initialize(String[] applicationArgs) {

  }

  public boolean hasUri() {
    return false;
  }

  public URI consumeUri() {
    return null;
  }

  public void setUrl(URI uri) {

  }
}
