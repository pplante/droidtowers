/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

public class DebugPurchaseManager extends PlatformPurchaseManger {
  @Override
  public void onStart() {
  }

  @Override
  public void onResume() {
  }

  @Override
  public boolean hasPurchasedUnlimitedVersion() {
    return true;
  }

  @Override
  public void requestPurchase(String itemId) {
  }
}
