/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.DesktopBrowserUtil;
import com.happydroids.platform.DesktopUncaughtExceptionHandler;
import com.happydroids.platform.Platform;

public class DesktopGame {
  public static void main(String[] args) {
    TowerGameService.setDeviceOSName(Platform.getOSType().name());
    TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

    Thread.setDefaultUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = String.format("Droid Towers (%s)", TowerConsts.VERSION);
    config.resizable = false;
    config.width = 800;
    config.height = 480;
    config.useGL20 = true;
//    config.vSyncEnabled = false;

    TowerGame towerGame = new TowerGame();
    towerGame.setUncaughtExceptionHandler(new DesktopUncaughtExceptionHandler());
    towerGame.setPlatformBrowserUtil(new DesktopBrowserUtil());

    new LwjglApplication(new LwjglApplicationShim(towerGame), config);
  }

}
