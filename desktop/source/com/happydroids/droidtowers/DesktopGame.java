/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.PlatformProtocolHandlerFactory;
import com.happydroids.platform.*;

public class DesktopGame {
  public static void main(final String[] args) {
    PlatformQuitHandlerFactory.initialize();

    TowerGameService.setDeviceType(Platform.getOSType().name());
    TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = String.format("Droid Towers (v%s)", TowerConsts.VERSION);
    config.resizable = false;
    config.width = 800;
    config.height = 600;
    config.useGL20 = true;
//    config.vSyncEnabled = false;

    new LwjglApplication(new LwjglApplicationShim(new DroidTowersGame(new Runnable() {
      @Override
      public void run() {
        Platform.setProtocolHandler(PlatformProtocolHandlerFactory.newInstance());
        Platform.getProtocolHandler().initialize(args);

        Platform.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
        Platform.setBrowserUtil(new DesktopBrowserUtil());
        Platform.setConnectionMonitor(new DesktopConnectionMonitor());
        Platform.setPurchaseManager(new DebugPurchaseManager());
      }
    })), config);
  }
}
