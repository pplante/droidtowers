/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

public class DebugPurchaseManager extends PlatformPurchaseManger {
  @Override
  public void requestPurchase(String itemId) {
  }

  @Override
  public void enablePurchases() {
  }

  @Override
  public boolean hasPurchasedUnlimitedVersion() {
    return true;
  }
}
