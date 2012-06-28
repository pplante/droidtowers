/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.happydroids.droidtowers.DroidTowerGame;
import com.happydroids.platform.PlatformPurchaseManger;

public class AndroidPurchaseManager extends PlatformPurchaseManger {
  private static DroidTowerGame droidTowerGame;
  private static DroidTowerGame activity;


  public static void setActivity(DroidTowerGame activity) {
    AndroidPurchaseManager.droidTowerGame = activity;
  }

  @Override
  public void requestPurchase(String itemId) {
    droidTowerGame.requestPurchase(itemId);
  }

  @Override
  public void enablePurchases() {

  }
}
