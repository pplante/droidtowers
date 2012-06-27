/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.security.SecurePreferences;

public abstract class PlatformPurchaseManger {
  public static final String DROIDTOWERS_VERSION_UNLIMITED = "droidtowers.version.unlimited";
  protected final SecurePreferences purchases;

  public PlatformPurchaseManger() {
    purchases = new SecurePreferences("purchases");
  }

  public void purchaseItem(String itemId) {
    purchases.putBoolean(itemId, true);
    purchases.flush();
  }

  public void revokeItem(String itemId) {
    purchases.putBoolean(itemId, false);
    purchases.flush();
  }

  public boolean hasPurchasedUnlimitedVersion() {
    return purchases.getBoolean(DROIDTOWERS_VERSION_UNLIMITED, false);
  }

  public abstract void requestPurchase(String itemId);
  public abstract void enablePurchases();

  public void requestPurchaseForUnlimitedVersion() {
    requestPurchase(DROIDTOWERS_VERSION_UNLIMITED);
  }
}
