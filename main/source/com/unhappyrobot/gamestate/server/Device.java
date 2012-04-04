/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerConsts;

public class Device extends HappyDroidServiceObject {
  public String uuid;
  public String type;
  public String osVersion;
  private String appVersion;
  public boolean isAuthenticated;

  public Device() {
    uuid = HappyDroidService.instance().getDeviceId();
    type = HappyDroidService.getDeviceType();
    osVersion = HappyDroidService.getDeviceOSVersion();
    appVersion = TowerConsts.VERSION;
  }

  @Override
  public String getBaseResourceUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/register-device/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
