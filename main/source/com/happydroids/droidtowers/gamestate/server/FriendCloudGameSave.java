/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gamestate.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.NonInteractiveGameSave;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FriendCloudGameSave extends CloudGameSave {
  private PlayerProfile owner;

  public FriendCloudGameSave() {
    super();
  }

  public FriendCloudGameSave(String resourceUri) {
    this();
    setResourceUri(resourceUri);
  }

  @Override
  public String getBaseResourceUri() {
    return HappyDroidConsts.HAPPYDROIDS_URI + "/api/v1/friendgamesave/";
  }

  @Override
  public GameSave getGameSave() {
    return new NonInteractiveGameSave(super.getGameSave());
  }

  public PlayerProfile getOwner() {
    return owner;
  }

  @Override
  public String toString() {
    return "FriendCloudGameSave{" +
                   "owner=" + owner +
                   '}';
  }
}
