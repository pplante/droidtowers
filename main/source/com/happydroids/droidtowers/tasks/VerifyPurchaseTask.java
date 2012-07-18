/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.tasks;

import com.badlogic.gdx.Gdx;
import com.happydroids.droidtowers.gamestate.server.PaymentCollection;
import com.happydroids.droidtowers.gui.ProgressDialog;
import com.happydroids.platform.Platform;
import com.happydroids.server.Payment;
import com.happydroids.utils.BackgroundTask;

public class VerifyPurchaseTask extends BackgroundTask {
  private final ProgressDialog progressDialog;
  private PaymentCollection result;

  public VerifyPurchaseTask(String serial, ProgressDialog progressDialog) {
    result = new PaymentCollection();
    result.filterBy("serial", serial);
    this.progressDialog = progressDialog;
  }

  @Override
  protected void execute() throws Exception {
    result.fetch();
  }

  @Override
  public synchronized void afterExecute() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        try {
          if (!result.isEmpty()) {
            for (Payment payment : result.getObjects()) {
              if (!payment.wasRefunded()) {
                Platform.getPurchaseManager().purchaseItem(payment.getItemId(), payment.getSerial());
              } else {
                Platform.getPurchaseManager().revokeItem(payment.getItemId());
              }
            }
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
