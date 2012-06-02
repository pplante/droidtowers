/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceCollection;

public class FriendCloudGameSaveCollection extends HappyDroidServiceCollection<FriendCloudGameSave> {
  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/friendgamesave/";
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public FriendCloudGameSaveCollection() {
    super();
  }
}
