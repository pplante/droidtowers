/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
          InetAddress happyDroidServer = InetAddress.getByName(HappyDroidConsts.HAPPYDROIDS_SERVER);
          if (happyDroidServer.isReachable(1500)) {
            HttpResponse response = TowerGameService.instance().makeGetRequest(HappyDroidConsts.HAPPYDROIDS_URI + "/ping", null);
            if (response != null) {
              networkState = response.getStatusLine().getStatusCode() == 200;

              if (networkState) {
                runAllPostConnectRunnables();
              }
            }
          }
        } catch (UnknownHostException ignored) {
        } catch (IOException ignored) {
        }
        try {
          Thread.sleep(300000);
        } catch (InterruptedException ignored) {
        }
      }
    }
  };
}
