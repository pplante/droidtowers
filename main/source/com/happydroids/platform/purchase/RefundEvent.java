/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform.purchase;

public class RefundEvent {
  public final String itemId;

  public RefundEvent(String itemId) {
    this.itemId = itemId;
  }
}
