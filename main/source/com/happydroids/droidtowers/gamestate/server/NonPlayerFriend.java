/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NonPlayerFriend extends TowerGameServiceObject {
  private String friendName;
  private PlayerProfileProvider provider;

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/nonplayerfriend/";
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public String getFriendName() {
    return friendName;
  }

  public PlayerProfileProvider getProvider() {
    return provider;
  }

  @Override
  public String toString() {
    return "NonPlayerFriend{" +
                   "friendName='" + friendName + '\'' +
                   ", provider=" + provider +
                   '}';
  }

  public String getFirstName() {
    if (friendName.contains(" ")) {
      return friendName.substring(0, friendName.indexOf(" "));
    }

    return getFriendName();
  }
}
