/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

import java.util.Date;

@SuppressWarnings("FieldCanBeLocal")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Payment extends HappyDroidServiceObject {
  protected String itemId;
  protected String orderId;
  protected String source;
  protected Date receivedOn;
  protected Date refundedOn;
  protected String serial;

  public Payment(String itemId, String orderId, String source) {
    this.itemId = itemId;
    this.orderId = orderId;
    this.source = source;
  }

  public Payment() {

  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/payment/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  public boolean wasRefunded() {
    return refundedOn != null;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getItemId() {
    return itemId;
  }

  public String getSerial() {
    return serial;
  }
}
