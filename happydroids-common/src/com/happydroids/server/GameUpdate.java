/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.happydroids.HappyDroidConsts;

import java.util.Date;

public class GameUpdate extends HappyDroidServiceObject {
  public Date releasedOn;
  public String gitSha;
  public String version;
  public String notes;

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/gameupdate/";
  }

  @Override
  protected boolean requireAuthentication() {
    return false;
  }
}
