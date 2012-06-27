/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.widget.Toast;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.*;
import com.happydroids.platform.purchase.DummyPurchaseManager;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;

public class DroidTowerGame extends AndroidApplication implements BillingController.IConfiguration {
  private static final String TAG = DroidTowerGame.class.getSimpleName();

  private AbstractBillingObserver mBillingObserver;

  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Platform.setConnectionMonitor(new AndroidConnectionMonitor(this));
    Platform.setUncaughtExceptionHandler(new AndroidUncaughtExceptionHandler(this));
    Platform.setBrowserUtil(new AndroidBrowserUtil(this));
    Platform.setPurchaseManager(new DummyPurchaseManager(this));

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    Display.setHDPI(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH);
    Display.setScaledDensity(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? 1.5f : 1f);

    TowerGameService.setDeviceOSName("android");
    TowerGameService.setDeviceOSVersion("sdk" + getVersion());

    initialize(new TowerGame(), true);

    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);

    setupAndroidBilling();
  }

  private void setupAndroidBilling() {
    mBillingObserver = new AbstractBillingObserver(this) {
      @Override
      public void onBillingChecked(boolean supported) {
        DroidTowerGame.this.onBillingChecked(supported);
      }

      @Override
      public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
        DroidTowerGame.this.onPurchaseStateChanged(itemId, state);
      }

      @Override
      public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {
        DroidTowerGame.this.onRequestPurchaseResponse(itemId, response);
      }
    };
    BillingController.registerObserver(mBillingObserver);
    BillingController.setConfiguration(this); // This activity will provide
    BillingController.setDebug(HappyDroidConsts.DEBUG);
    // the public key and salt
    this.checkBillingSupported();
    if (!mBillingObserver.isTransactionsRestored()) {
      BillingController.restoreTransactions(this);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BillingController.unregisterObserver(mBillingObserver); // Avoid
    // receiving
    // notifications after
    // destroy
    BillingController.setConfiguration(null);
  }

  private void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {

  }

  private void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
    PlatformPurchaseManger purchaseManager = Platform.getPurchaseManager();

    Gdx.app.error(TAG, "Purchase of: " + itemId + " state: " + state.name());

    switch (state) {
      case PURCHASED:
        purchaseManager.purchaseItem(itemId);
        break;
      default:
        purchaseManager.revokeItem(itemId);
        break;
    }
  }

  private void onBillingChecked(boolean supported) {
    if (supported) {
      restoreTransactions();
      Platform.getPurchaseManager().enablePurchases();
    } else {
      new AlertDialog.Builder(this)
              .setTitle("Purchases via Google Play")
              .setMessage("Sorry but this device is unable to make purchases via Google Play.")
              .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
                }
              })
              .show();
    }
  }

  public BillingController.BillingStatus checkBillingSupported() {
    return BillingController.checkBillingSupported(this);
  }

  public void requestPurchase(String itemId) {
    BillingController.requestPurchase(this, itemId);
  }

  /**
   * Requests to restore all transactions.
   */
  public void restoreTransactions() {
    if (!mBillingObserver.isTransactionsRestored()) {
      BillingController.restoreTransactions(this);
      Toast.makeText(this, "Restoring previously purchased items.", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public byte[] getObfuscationSalt() {
    return "ad076e981c2ea4103f1a6e30b5e8d0bd81bca536".getBytes();
  }

  @Override
  public String getPublicKey() {
    return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr1epMa3vopbqUJAfVe90GqfjfYUQB7Edb5fBUfTyLJ6lXQORZyvpiF+vTtCA0FEHI4jB9V4TMaJcFrnTk5MZDUHi1zkj0cSn9OG7znzEvSFwfJ63b/UWBZIdgx5/bE63Mkv3LL87aNFWlg5TzgR7mQtIxHjP4iP0y4mxJJujt49ArFxYWoIIBZCv0e5zyUtQDLPYfirp3nNUPLg/wW1VNeUutkR+71r6+z/a1MeKMfUzVOoSJisnNhqWhlSkrN4Mlz5ehJhDt/ubf9n0AFafusGnmrdYFwGrOjpWDCkOpLEvkvlZiNV+sshRVaRUCwFKPBbjV/NFsDKlkdgZnms2WwIDAQAB";
  }
}
