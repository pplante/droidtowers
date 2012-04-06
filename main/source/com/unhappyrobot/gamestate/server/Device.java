/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidService;
import com.happydroids.server.HappyDroidServiceObject;

public class Device extends HappyDroidServiceObject {
  public String uuid;
  public String type;
  public String osVersion;
  private String appVersion;
  public boolean isAuthenticated;

  public Device() {
    uuid = TowerGameService.instance().getDeviceId();
    type = HappyDroidService.getDeviceType();
    osVersion = HappyDroidService.getDeviceOSVersion();
    appVersion = HappyDroidConsts.VERSION;
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
