package com.unhappyrobot.gamestate.server;

import com.unhappyrobot.TowerConsts;

import java.util.Date;

public class GameUpdate extends HappyDroidServiceObject {
  public Date releasedOn;
  public String gitSha;
  public String version;
  public String notes;

  @Override
  public String getBaseResourceUri() {
    return TowerConsts.HAPPYDROIDS_URI + "/api/v1/gameupdate/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
