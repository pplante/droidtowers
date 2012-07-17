/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.platform.purchase.DroidTowerVersions;

import java.util.UUID;

public class DebugPurchaseManager extends PlatformPurchaseManger {
  public DebugPurchaseManager() {
    super();
    itemSkus.put(DroidTowerVersions.UNLIMITED_299, DroidTowerVersions.UNLIMITED_299.name());
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }


  @Override
  public void requestPurchase(String itemId) {
    purchaseItem(itemId, UUID.randomUUID().toString());
  }
}
