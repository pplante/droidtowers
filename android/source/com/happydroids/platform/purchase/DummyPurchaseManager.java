/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.happydroids.droidtowers.DroidTowerGame;
import com.happydroids.platform.PlatformPurchaseManger;
import com.happydroids.security.SecurePreferences;

public class DummyPurchaseManager implements PlatformPurchaseManger {
  private final DroidTowerGame droidTowerGame;
  private final SecurePreferences purchases;

  public DummyPurchaseManager(DroidTowerGame droidTowerGame) {
    this.droidTowerGame = droidTowerGame;
    purchases = new SecurePreferences("purchases");
  }

  @Override
  public void requestPurchase(String itemId) {
    droidTowerGame.requestPurchase(itemId);
  }

  @Override
  public void purchaseItem(String itemId) {
    purchases.putBoolean(itemId, true);
  }

  @Override
  public void revokeItem(String itemId) {
    purchases.putBoolean(itemId, false);
  }

  @Override
  public void enablePurchases() {

  }
}
