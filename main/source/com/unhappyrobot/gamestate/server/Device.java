package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerConsts;

public class Device extends HappyDroidServiceObject {
  public String uuid;
  public String type;
  public String osVersion;
  private String appVersion;
  public boolean isAuthenticated;
  public boolean updateAvailable;

  public Device() {
    uuid = HappyDroidService.instance().getDeviceId();
    type = HappyDroidService.getDeviceType();
    osVersion = HappyDroidService.getDeviceOSVersion();
    appVersion = TowerConsts.VERSION;
  }

  @Override
  protected String getResourceBaseUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/register-device/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
