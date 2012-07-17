/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tasks;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gui.ProgressDialog;
import com.happydroids.platform.Platform;
import com.happydroids.server.Payment;
import com.happydroids.utils.BackgroundTask;

public class VerifyPurchaseTask extends BackgroundTask {
  private final ProgressDialog progressDialog;
  private Payment payment;

  public VerifyPurchaseTask(String paymentUri, ProgressDialog progressDialog) {
    payment = new Payment();
    payment.setResourceUri(paymentUri);
    this.progressDialog = progressDialog;
  }

  @Override
  protected void execute() throws Exception {
    payment.fetch();
  }

  @Override
  public synchronized void afterExecute() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        try {
          if (!payment.wasRefunded()) {
            Platform.getPurchaseManager().purchaseItem(payment.getItemId(), payment.getOrderId());
          } else {
            Platform.getPurchaseManager().revokeItem(payment.getItemId());
          }
        } catch (NullPointerException ignored) {
          // TODO: FIX THIS SHIT.
        }
      }
    });

    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
