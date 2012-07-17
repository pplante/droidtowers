/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.purchase.DroidTowerVersions;

public class DesktopPurchaseManager extends PlatformPurchaseManger {
  public DesktopPurchaseManager() {
    super();

    itemSkus.put(DroidTowerVersions.UNLIMITED_299, "com.happydroids.droidtowers.version.unlimited299");
  }

  @Override
  public void requestPurchase(String itemId) {
    Platform.getBrowserUtil().launchWebBrowser(HappyDroidConsts.HAPPYDROIDS_URI + "/droidtowers/purchase");
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }
}
