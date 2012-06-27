/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.badlogic.gdx.Gdx;

public class MacQuitHandler {
  public MacQuitHandler() {
    Application.getApplication().setQuitHandler(new QuitHandler() {
      @Override
      public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
        Gdx.app.exit();
      }
    });
  }
}
