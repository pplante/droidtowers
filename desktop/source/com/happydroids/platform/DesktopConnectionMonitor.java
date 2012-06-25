package com.happydroids.platform;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import com.happydroids.HappyDroidConsts;

import java.io.IOException;
import java.net.InetAddress;

public class DesktopConnectionMonitor implements PlatformConnectionMonitor {
  private boolean checkedBefore;
  private boolean networkState;
  private final Thread monitorThread;

  public DesktopConnectionMonitor() {
    monitorThread = new Thread() {
      @Override
      public void run() {
        try {
          networkState = InetAddress.getByName(HappyDroidConsts.HAPPYDROIDS_SERVER).isReachable(50);
        } catch (IOException e) {
          networkState = false;
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
