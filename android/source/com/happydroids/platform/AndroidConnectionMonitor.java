/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidConnectionMonitor implements PlatformConnectionMonitor {
  private final Context context;

  public AndroidConnectionMonitor(Context context) {
    this.context = context;
  }

  public boolean isConnectedOrConnecting() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null) {
      NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    return false;
  }
}
