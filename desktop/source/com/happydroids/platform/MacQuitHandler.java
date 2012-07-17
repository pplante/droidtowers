/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.apple.eawt.*;
import com.badlogic.gdx.Gdx;

public class MacQuitHandler {
  public MacQuitHandler() {
    Application.getApplication().setQuitHandler(new QuitHandler() {
      @Override
      public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
        Gdx.app.exit();
      }
    });
    Application.getApplication().setQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);
  }
}
