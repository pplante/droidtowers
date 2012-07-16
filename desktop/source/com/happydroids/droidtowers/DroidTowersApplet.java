/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.ProgressDialog;
import com.happydroids.droidtowers.platform.purchase.AppletPurchaseManager;
import com.happydroids.droidtowers.tasks.VerifyPurchaseTask;
import com.happydroids.platform.DesktopBrowserUtil;
import com.happydroids.platform.DesktopConnectionMonitor;
import com.happydroids.platform.DesktopUncaughtExceptionHandler;
import com.happydroids.platform.Platform;
import netscape.javascript.JSObject;

public class DroidTowersApplet extends LwjglApplet {

  public static final String TAG = DroidTowersApplet.class.getSimpleName();

  public DroidTowersApplet() {
    super(new DroidTowersGame(new Runnable() {
      private DroidTowersApplet droidTowersApplet;

      @Override
      public void run() {
        TowerGameService.setDeviceOSMarketName("web-applet");
        TowerGameService.setDeviceType(Platform.getOSType().name());
        TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

        Platform.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
        Platform.setBrowserUtil(new DesktopBrowserUtil());
        Platform.setConnectionMonitor(new DesktopConnectionMonitor());
        Platform.setPurchaseManager(new AppletPurchaseManager());
      }
    }), true);
  }

  @Override
  public void init() {
    final JSObject javascript = JSObject.getWindow(this);
    final JSObject happyDroids = (JSObject) javascript.getMember("happyDroids");
    Gdx.app.error(TAG, "session: " + happyDroids);

    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        TowerGameService.setDeviceOSMarketName((String) happyDroids.getMember("marketName"));
        TowerGameService.instance().setDeviceId((String) happyDroids.getMember("deviceUUID"));
        TowerGameService.instance().setSessionToken((String) happyDroids.getMember("sessionToken"));

        ((AppletPurchaseManager) Platform.getPurchaseManager()).setJavascriptInterface(happyDroids);
      }
    });
  }

  public void purchaseComplete(final String paymentUri) {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.hideButtons(true)
                .setMessage("Verifying Purchase")
                .show();
        new VerifyPurchaseTask(paymentUri, progressDialog).run();
      }
    });
  }
}
