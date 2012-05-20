/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.happydroids.HappyDroidConsts;
import com.happydroids.server.HappyDroidServiceCollection;

public class NonPlayerFriendCollection extends HappyDroidServiceCollection<NonPlayerFriend> {
  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/nonplayerfriend/";
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public NonPlayerFriendCollection() {
    super(NonPlayerFriend.class);
  }
}
