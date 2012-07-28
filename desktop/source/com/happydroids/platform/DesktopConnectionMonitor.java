/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apache.http.HttpResponse;

public class DesktopConnectionMonitor extends PlatformConnectionMonitor {
  private boolean checkedBefore;
  private boolean networkState;

  public DesktopConnectionMonitor() {
    monitorThread.start();
  }

  @Override
  public boolean isConnectedOrConnecting() {
    return networkState;
  }

  @SuppressWarnings("FieldCanBeLocal")
  private final Thread monitorThread = new Thread() {
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
      while (true) {
        networkState = false;
        try {
          HttpResponse response = TowerGameService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + "/ping", null);
          if (response != null) {
            networkState = response.getStatusLine().getStatusCode() == 200;

            if (networkState) {
              runAllPostConnectRunnables();
            }
          }
        } catch (Throwable ignored) {
        }
        try {
          Thread.sleep(60000);
        } catch (InterruptedException ignored) {
        }
      }
    }
  };
}
