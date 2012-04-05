/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.unhappyrobot.gamestate.server.TowerGameService;
import com.unhappyrobot.platform.DesktopBrowserUtil;
import com.unhappyrobot.platform.DesktopUncaughtExceptionHandler;
import com.unhappyrobot.utils.OSValidator;

public class DesktopGame {
  public static void main(String[] args) {
    TowerGameService.setDeviceOSName(OSValidator.getOSType());
    TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.title = "TowerSim";
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
