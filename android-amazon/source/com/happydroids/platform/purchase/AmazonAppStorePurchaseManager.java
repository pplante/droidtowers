/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

import com.amazon.inapp.purchasing.PurchasingManager;
import com.amazon.inapp.purchasing.PurchasingObserver;
import com.happydroids.droidtowers.amazon.DroidTowersAmazon;
import com.happydroids.platform.PlatformPurchaseManger;

import static com.happydroids.platform.purchase.DroidTowerVersions.UNLIMITED_299;

public class AmazonAppStorePurchaseManager extends PlatformPurchaseManger {
  private PurchasingObserver purchaseObserver;
  private boolean registeredObserver;

  public AmazonAppStorePurchaseManager(DroidTowersAmazon droidTowersAmazon) {
    super();
    purchaseObserver = new AmazonPurchaseObserver(droidTowersAmazon);

    itemSkus.put(UNLIMITED_299, "com.happydroids.droidtowers.amazon.purchase.unlimited299");
  }

  @Override
  public void requestPurchase(String itemSku) {
    getPurchases().putString(PurchasingManager.initiatePurchaseRequest(itemSku), itemSku);
    getPurchases().flush();
  }

  @Override
  public void onStart() {
    if (!registeredObserver) {
      registeredObserver = true;
      PurchasingManager.registerObserver(purchaseObserver);
    }
  }

  @Override
  public void onResume() {
    onStart();

    PurchasingManager.initiateGetUserIdRequest();
  }

  public PurchasingObserver getPurchaseObserver() {
    return purchaseObserver;
  }
}
