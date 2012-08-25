/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class AndroidBrowserUtil implements PlatformBrowserUtil {
  private final Activity activity;

  public AndroidBrowserUtil(Activity activity) {
    this.activity = activity;
  }

  public void launchWebBrowser(final String uriToLoad) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (uriToLoad.startsWith("market") || uriToLoad.startsWith("amzn") || uriToLoad.contains("youtube.com")) {
          //Pass it to the system, doesn't match your domain
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(uriToLoad));
          activity.startActivity(intent);
        } else {
          new TowerWebBrowser(activity, uriToLoad).show();
        }
      }
    });
  }
}
