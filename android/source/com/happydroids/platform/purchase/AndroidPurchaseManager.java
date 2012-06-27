/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.happydroids.droidtowers.DroidTowerGame;
import com.happydroids.platform.PlatformPurchaseManger;

public class AndroidPurchaseManager extends PlatformPurchaseManger {
  private final DroidTowerGame droidTowerGame;

  public AndroidPurchaseManager(DroidTowerGame droidTowerGame) {
    super();
    this.droidTowerGame = droidTowerGame;
  }

  @Override
  public void requestPurchase(String itemId) {
    droidTowerGame.requestPurchase(itemId);
  }

  @Override
  public void enablePurchases() {

  }
}
