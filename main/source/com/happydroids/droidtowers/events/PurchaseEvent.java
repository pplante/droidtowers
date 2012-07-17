/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

public class PurchaseEvent {
  private final String purchaseToken;
  private final String itemId;

  public PurchaseEvent(String purchaseToken, String itemId) {
    this.purchaseToken = purchaseToken;
    this.itemId = itemId;
  }

  public String itemId() {
    return itemId;
  }

  public String orderId() {
    return purchaseToken;
  }
}
