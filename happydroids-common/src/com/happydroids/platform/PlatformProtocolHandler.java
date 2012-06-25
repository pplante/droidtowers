/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import java.net.URI;

public interface PlatformProtocolHandler {
  public void initialize(String[] applicationArgs);

  public boolean hasUri();

  public URI consumeUri();

  void setUrl(URI uri);
}
