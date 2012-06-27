/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

public interface PlatformPurchaseManger {
  public void requestPurchase(String itemId);
  public void purchaseItem(String itemId);
  public void revokeItem(String itemId);
  public void enablePurchases();
}
