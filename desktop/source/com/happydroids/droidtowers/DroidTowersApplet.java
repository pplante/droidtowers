/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplet;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.platform.*;
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
        Platform.setPurchaseManager(new DebugPurchaseManager());
      }
    }), new LwjglApplicationConfiguration());
  }

  @Override
  public void init() {
    JSObject jsObject = JSObject.getWindow(this);
    String happyDroidsSession = (String) jsObject.getMember("happyDroidsSession");
    Gdx.app.error(TAG, "session: " + happyDroidsSession);
  }
}
