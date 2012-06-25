/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.content.Context;
import android.net.ConnectivityManager;

public class AndroidConnectionMonitor implements PlatformConnectionMonitor {
  private final Context context;

  public AndroidConnectionMonitor(Context context) {
    this.context = context;
  }

  public boolean isConnectedOrConnecting() {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo().isConnectedOrConnecting();
  }
}
