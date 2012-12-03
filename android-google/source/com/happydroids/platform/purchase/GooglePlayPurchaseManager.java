/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.happydroids.droidtowers.DroidTowersGooglePlay;
import com.happydroids.platform.PlatformPurchaseManger;

import static com.happydroids.platform.purchase.DroidTowerVersions.*;

public class GooglePlayPurchaseManager extends PlatformPurchaseManger {
  private DroidTowersGooglePlay droidTowersGooglePlay;


  public GooglePlayPurchaseManager(DroidTowersGooglePlay droidTowersGooglePlay) {
    this.droidTowersGooglePlay = droidTowersGooglePlay;
    itemSkus.put(UNLIMITED_299, "com.happydroids.droidtowers.version.unlimited299");
    itemSkus.put(ONE_TIME_DISCOUNT_199, "com.happydroids.droidtowers.version.unlimited199");
    itemSkus.put(UNLIMITED_99_XMAS, "com.happydroids.droidtowers.version.unlimited99.xmas");
  }

  @Override
  public void requestPurchase(String itemId) {
    droidTowersGooglePlay.requestPurchase(itemId);
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }
}
