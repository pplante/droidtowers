/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NonPlayerFriend extends TowerGameServiceObject {
  private int id;
  private String friendName;
  private PlayerProfileProvider provider;

  @Override
  public String getBaseResourceUri() {
    throw new RuntimeException("NonPlayerFriend cannot be directly accessed!");
  }

  @Override
  protected boolean requireAuthentication() {
    return true;
  }

  public int getId() {
    return id;
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
                   ", id=" + id +
                   ", provider=" + provider +
                   '}';
  }
}
