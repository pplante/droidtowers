/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenURIHandler;

import java.net.URI;

public class MacProtocolHandler implements PlatformProtocolHandler {
  private URI uri;

  public void initialize(String[] applicationArgs) {
    Application.getApplication().setOpenURIHandler(new OpenURIHandler() {
      public void openURI(AppEvent.OpenURIEvent openURIEvent) {
        uri = openURIEvent.getURI();
      }
    });
  }

  public boolean hasUri() {
    return uri != null;
  }
}
