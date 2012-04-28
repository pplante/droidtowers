/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

public interface PlatformProtocolHandler {
  public void initialize(String[] applicationArgs);

  public boolean hasUri();
}
