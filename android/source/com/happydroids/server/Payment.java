/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Payment extends HappyDroidServiceObject {
  protected final String itemId;
  protected final String orderId;
  protected final String source;

  public Payment(String itemId, String orderId, String source) {
    this.itemId = itemId;
    this.orderId = orderId;
    this.source = source;
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/payment/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
