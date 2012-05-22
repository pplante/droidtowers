/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.friends;

import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;

public class NonPlayerFriendItem extends PlayerFriendItem {
  public final NonPlayerFriend profile;

  public NonPlayerFriendItem(NonPlayerFriend profile) {
    this.profile = profile;
  }

  @Override
  protected String getPlayerName() {
    return profile.getFriendName();
  }

  @Override
  public boolean playerNameMatches(String text) {
    return profile.getFriendName().toLowerCase().contains(text);
  }
}
