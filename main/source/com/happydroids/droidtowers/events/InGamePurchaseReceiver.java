/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.google.common.eventbus.Subscribe;
import com.happydroids.droidtowers.gui.PurchaseAppreciationDialog;
import com.happydroids.server.Payment;
import com.happydroids.utils.BackgroundTask;

public class InGamePurchaseReceiver {
  @Subscribe
  public void PurchaseManager_onPurchase(final PurchaseEvent event) {
    new PurchaseAppreciationDialog().show();

    new BackgroundTask() {
      @Override
      protected void execute() throws Exception {
        new Payment(event.itemId(), event.orderId(), event.sourceMarket()).save();
      }
    }.run();
  }
}
