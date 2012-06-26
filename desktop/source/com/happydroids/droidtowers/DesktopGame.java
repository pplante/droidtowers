/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.platform.DesktopBrowserUtil;
import com.happydroids.droidtowers.platform.PlatformProtocolHandlerFactory;
import com.happydroids.platform.DesktopConnectionMonitor;
import com.happydroids.platform.DesktopUncaughtExceptionHandler;
import com.happydroids.platform.Platform;

public class DesktopGame {
  public static void main(String[] args) {
    Platform.setProtocolHandler(PlatformProtocolHandlerFactory.newInstance());
    Platform.getProtocolHandler().initialize(args);

    Platform.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
    Platform.setBrowserUtil(new DesktopBrowserUtil());
    Platform.setConnectionMonitor(new DesktopConnectionMonitor());

    TowerGameService.setDeviceOSName(Platform.getOSType().name());
    TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = String.format("Droid Towers (%s)", TowerConsts.VERSION);
    config.resizable = false;
    config.width = 1280;
    config.height = 800;
    config.useGL20 = true;
//    config.vSyncEnabled = false;

    new LwjglApplication(new LwjglApplicationShim(new TowerGame()), config);
  }

}
