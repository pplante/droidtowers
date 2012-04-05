/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceObject;
import com.unhappyrobot.TowerConsts;

public class Device extends HappyDroidServiceObject {
  public String uuid;
  public String type;
  public String osVersion;
  private String appVersion;
  public boolean isAuthenticated;

  public Device() {
    uuid = TowerGameService.instance().getDeviceId();
    type = TowerGameService.getDeviceType();
    osVersion = TowerGameService.getDeviceOSVersion();
    appVersion = TowerConsts.VERSION;
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/register-device/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
