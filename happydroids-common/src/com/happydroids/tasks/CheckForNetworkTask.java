/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.tasks;

import com.badlogic.gdx.Gdx;
import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidService;
import com.happydroids.utils.BackgroundTask;

import java.io.IOException;
import java.net.InetAddress;

public class CheckForNetworkTask extends BackgroundTask {
  private static String TAG = CheckForNetworkTask.class.getSimpleName();

  @Override
  protected void execute() throws Exception {
    try {
      InetAddress remote = InetAddress.getByName(HappyDroidConsts.HAPPYDROIDS_SERVER);
      HappyDroidService.setConnectionState(remote.isReachable(1500));
      Gdx.app.debug(TAG, "Network status: " + HappyDroidService.instance().haveNetworkConnection());
    } catch (IOException e) {
      Gdx.app.error(TAG, "Network error!", e);
    }
  }

  @Override
  public synchronized void afterExecute() {
    HappyDroidService.instance().runAfterNetworkCheckRunnables();
  }
}
