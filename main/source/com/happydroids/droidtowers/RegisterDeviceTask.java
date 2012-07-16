/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gamestate.server.Device;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.utils.BackgroundTask;

import static com.badlogic.gdx.Application.ApplicationType.Applet;

public class RegisterDeviceTask extends BackgroundTask {
  protected static final String TAG = RegisterDeviceTask.class.getSimpleName();

  private Device device;

  @Override
  protected void execute() throws Exception {
    device = new Device();
    device.save();
  }

  @Override
  public synchronized void afterExecute() {
    if (Gdx.app.getType().equals(Applet)) {
      // applets are authenticated via javascript :D
      return;
    }

    Gdx.app.debug(TAG, "Authentication finished, state: " + device.isAuthenticated);
    TowerGameService.instance().setAuthenticated(device.isAuthenticated);
  }
}
