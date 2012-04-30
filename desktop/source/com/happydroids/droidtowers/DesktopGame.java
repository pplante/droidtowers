/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.platform.DesktopBrowserUtil;
import com.happydroids.droidtowers.platform.PlatformProtocolHandler;
import com.happydroids.droidtowers.platform.PlatformProtocolHandlerFactory;
import com.happydroids.platform.DesktopUncaughtExceptionHandler;
import com.happydroids.platform.Platform;

import java.net.URI;
import java.net.URISyntaxException;

public class DesktopGame {
  public static void main(String[] args) {
    PlatformProtocolHandler protocolHandler = PlatformProtocolHandlerFactory.newInstance();
    protocolHandler.initialize(args);

    try {
      protocolHandler.setUrl(new URI("droidtowers://launchgame?id=4"));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    TowerGameService.setDeviceOSName(Platform.getOSType().name());
    TowerGameService.setDeviceOSVersion(System.getProperty("os.version"));

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
    towerGame.setProtocolHandler(protocolHandler);

    new LwjglApplication(new LwjglApplicationShim(towerGame), config);
  }

}
