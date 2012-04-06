/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

import java.io.IOException;

@SuppressWarnings("ALL")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CrashReport extends HappyDroidServiceObject {
  private final String name;
  private final String version = HappyDroidConsts.VERSION;
  private final String gitSha = HappyDroidConsts.GIT_SHA;
  private final String deviceOsVersion;
  private final String deviceType;
  private final String message;
  private String stackTrace;
  //  private GameSave gameState;
  private final String cause;

  public CrashReport(Throwable error) {
    deviceType = HappyDroidService.getDeviceType();
    deviceOsVersion = HappyDroidService.getDeviceOSVersion();
    name = error.getClass().getCanonicalName();
    message = error.getMessage();
    cause = error.getCause().toString();
    try {
      stackTrace = HappyDroidService.instance().getObjectMapper().writeValueAsString(error.getStackTrace());
    } catch (IOException e) {
      e.printStackTrace();
    }
//    gameState = null;

//    if (TowerGame.getActiveScene() instanceof TowerScene) {
//      ((TowerScene) TowerGame.getActiveScene()).getCurrentGameSave().update();
//      gameState = ((TowerScene) TowerGame.getActiveScene()).getCurrentGameSave();
//    }
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/crashreport/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
