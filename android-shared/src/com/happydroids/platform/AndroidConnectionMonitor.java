/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.happydroids.HappyDroidConsts;

import java.io.IOException;
import java.net.InetAddress;

public class AndroidConnectionMonitor implements PlatformConnectionMonitor {
  private boolean checkedBefore;
  private boolean networkState;
  private final Activity context;

  public AndroidConnectionMonitor(Activity context) {
    this.context = context;
    monitorThread.start();
  }

  @Override
  public boolean isConnectedOrConnecting() {
    return networkState;
  }

  @SuppressWarnings("FieldCanBeLocal")
  private final Thread monitorThread = new Thread() {
    @Override
    public void run() {
      while (!context.isFinishing()) {
        try {
          if (haveNetworkConnection()) {
            networkState = InetAddress.getByName(HappyDroidConsts.HAPPYDROIDS_SERVER).isReachable(1500);
          }
        } catch (IOException e) {
          networkState = false;
        }

        try {
          Thread.yield();
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  private boolean haveNetworkConnection() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    return false;
  }
}
