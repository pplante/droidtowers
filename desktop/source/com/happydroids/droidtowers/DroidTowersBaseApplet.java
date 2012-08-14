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
import com.happydroids.platform.DesktopUncaughtExceptionHandler;
import com.happydroids.platform.Platform;
import com.happydroids.platform.PlatformConnectionMonitor;
import netscape.javascript.JSObject;
import org.apach3.commons.lang3.StringUtils;

public abstract class DroidTowersBaseApplet extends LwjglApplet {
  public static final String TAG = DroidTowersBaseApplet.class.getSimpleName();

  public DroidTowersBaseApplet(final String marketName) {
    super(new DroidTowersGame(new Runnable() {
      private DroidTowersBaseApplet droidTowersApplet;

      @Override
      public void run() {
        TowerGameService.setDeviceOSMarketName(marketName);
        TowerGameService.setDeviceType(Platform.getOSType().name());
        TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

        Platform.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
        Platform.setBrowserUtil(new DesktopBrowserUtil());
        Platform.setConnectionMonitor(new PlatformConnectionMonitor());
        Platform.setPurchaseManager(new AppletPurchaseManager());
      }
    }), true);
  }

  @Override
  public void init() {
    final JSObject javascript = JSObject.getWindow(this);
    final JSObject happyDroids = (JSObject) javascript.getMember("happyDroids");
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        TowerGameService.instance().setDeviceId((String) happyDroids.getMember("deviceUUID"));
        TowerGameService.instance().setSessionToken((String) happyDroids.getMember("sessionToken"));

        ((AppletPurchaseManager) Platform.getPurchaseManager()).setJavascriptInterface(happyDroids);

        String serial = (String) happyDroids.getMember("serial");
        if (!StringUtils.isEmpty(serial)) {
          new VerifyPurchaseTask(serial, null).run();
        }
      }
    });
  }

  public void purchaseComplete(final String serial) {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.hideButtons(true)
                .setMessage("Verifying Purchase")
                .show();
        new VerifyPurchaseTask(serial, progressDialog).run();
      }
    });
  }
}
