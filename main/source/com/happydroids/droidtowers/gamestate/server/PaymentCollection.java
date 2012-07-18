/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.server.Payment;

public class PaymentCollection extends HappyDroidServiceCollection<Payment> {
  @Override
  protected boolean requireAuthentication() {
    return false;
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/payment/";
  }
}
