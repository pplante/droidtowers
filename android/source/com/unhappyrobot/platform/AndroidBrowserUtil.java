/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.platform;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class AndroidBrowserUtil implements PlatformBrowserUtil {
  private final Activity activity;

  public AndroidBrowserUtil(Activity activity) {

    this.activity = activity;
  }

  public void launchWebBrowser(String uri) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    activity.startActivity(browserIntent);
  }
}
