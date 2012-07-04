/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.google.common.collect.Maps;
import com.happydroids.droidtowers.DroidTowersGooglePlay;
import com.happydroids.platform.PlatformPurchaseManger;

import java.util.HashMap;

import static com.happydroids.platform.purchase.DroidTowerVersions.UNLIMITED_299;

public class GooglePlayPurchaseManager extends PlatformPurchaseManger {
  private static DroidTowersGooglePlay droidTowersGooglePlay;
  private static DroidTowersGooglePlay activity;
  private final HashMap<Object, Object> itemSkus;


  public GooglePlayPurchaseManager() {
    itemSkus = Maps.newHashMap();
    itemSkus.put(UNLIMITED_299, "com.happydroids.droidtowers.version.unlimited299");
  }

  public static void setActivity(DroidTowersGooglePlay activity) {
    GooglePlayPurchaseManager.droidTowersGooglePlay = activity;
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
