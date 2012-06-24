/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import android.app.Activity;

public class AndroidBrowserUtil implements PlatformBrowserUtil {
  private final Activity activity;

  public AndroidBrowserUtil(Activity activity) {
    this.activity = activity;
  }

  public void launchWebBrowser(final String uriToLoad) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        new TowerWebBrowser(activity, uriToLoad).show();
      }
    });
  }
}
