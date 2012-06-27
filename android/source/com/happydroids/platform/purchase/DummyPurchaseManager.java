/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.google.common.collect.Sets;
import com.happydroids.droidtowers.DroidTowerGame;
import com.happydroids.platform.PlatformPurchaseManger;

import java.util.Set;

public class DummyPurchaseManager implements PlatformPurchaseManger {
  private Set<String> purchasedItems;
  private final DroidTowerGame droidTowerGame;

  public DummyPurchaseManager(DroidTowerGame droidTowerGame) {
    this.droidTowerGame = droidTowerGame;
    this.purchasedItems = Sets.newHashSet();
  }

  @Override
  public void requestPurchase(String itemId) {
    droidTowerGame.requestPurchase(itemId);
  }

  @Override
  public void purchaseItem(String itemId) {
    purchasedItems.add(itemId);
  }

  @Override
  public void revokeItem(String itemId) {
    purchasedItems.remove(itemId);
  }

  @Override
  public void enablePurchases() {

  }
}
