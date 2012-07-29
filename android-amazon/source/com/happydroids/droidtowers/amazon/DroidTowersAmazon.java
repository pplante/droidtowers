/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.amazon;


import android.os.Handler;
import android.util.DisplayMetrics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.happydroids.droidtowers.DroidTowersGame;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.platform.AndroidBrowserUtil;
import com.happydroids.platform.AndroidUncaughtExceptionHandler;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformConnectionMonitor;
import com.happydroids.platform.purchase.AmazonAppStorePurchaseManager;

public class DroidTowersAmazon extends AndroidApplication {
  private static final String TAG = DroidTowersAmazon.class.getSimpleName();
  private Handler handler;

  public void onCreate(android.os.Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handler = new Handler(getMainLooper());

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    Display.setXHDPI(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH);
    Display.setScaledDensity(metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH ? 1.5f : 1f);

    TowerGameService.setDeviceType("android");
    TowerGameService.setDeviceOSMarketName("amazon");
    TowerGameService.setDeviceOSVersion("sdk" + getVersion());

    initialize(new DroidTowersGame(new Runnable() {
      @Override
      public void run() {
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        Platform.setConnectionMonitor(new PlatformConnectionMonitor());
        Platform.setUncaughtExceptionHandler(new AndroidUncaughtExceptionHandler(DroidTowersAmazon.this));
        Platform.setBrowserUtil(new AndroidBrowserUtil(DroidTowersAmazon.this));
        Platform.setPurchaseManager(new AmazonAppStorePurchaseManager(DroidTowersAmazon.this));
      }
    }), true);
  }

  @Override
  protected void onStart() {
    super.onStart();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (Platform.getPurchaseManager() != null) {
          Platform.getPurchaseManager().onStart();
        } else {
          handler.postDelayed(this, 250);
        }
      }
    }, 250);
  }

  @Override
  protected void onResume() {
    super.onResume();

    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (Platform.getPurchaseManager() != null) {
          Platform.getPurchaseManager().onResume();
        } else {
          handler.postDelayed(this, 250);
        }
      }
    }, 250);
  }
}
