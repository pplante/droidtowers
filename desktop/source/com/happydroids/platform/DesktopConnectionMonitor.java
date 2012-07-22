/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apache.http.HttpResponse;

public class DesktopConnectionMonitor implements PlatformConnectionMonitor {
  private boolean checkedBefore;
  private boolean networkState;
  private final Thread monitorThread;

  public DesktopConnectionMonitor() {
    monitorThread = new Thread() {
      @SuppressWarnings("InfiniteLoopStatement")
      @Override
      public void run() {
        while (true) {
          HttpResponse response = TowerGameService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + "/ping", null);
          networkState = response.getStatusLine().getStatusCode() == 200;
          try {
            Thread.yield();
            Thread.sleep(300000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };

    monitorThread.start();
  }

  @Override
  public boolean isConnectedOrConnecting() {
    return networkState;
  }
}
