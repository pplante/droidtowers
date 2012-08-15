/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import android.app.Activity;
import com.heyzap.sdk.HeyzapLib;

public class HeyZapCheckInManager implements PlatformCheckInManager {
  private final Activity activity;

  public HeyZapCheckInManager(Activity activity) {
    this.activity = activity;
    HeyzapLib.load(activity, false);
  }

  @Override public void checkInNow() {
    HeyzapLib.checkin(activity);
  }

  @Override public void checkInNow(String message) {
    HeyzapLib.checkin(activity, message);
  }
}
