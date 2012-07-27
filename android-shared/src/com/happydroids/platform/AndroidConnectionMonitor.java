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
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AndroidConnectionMonitor extends PlatformConnectionMonitor {
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

  private boolean haveNetworkConnection() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    return false;
  }

  @SuppressWarnings("FieldCanBeLocal")
  private final Thread monitorThread = new Thread() {
    @Override
    public void run() {
      while (!context.isFinishing()) {
        networkState = false;

        if (haveNetworkConnection()) {
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
        }
        try {
          Thread.sleep(300000);
        } catch (InterruptedException ignored) {
        }
      }
    }
  };
}
