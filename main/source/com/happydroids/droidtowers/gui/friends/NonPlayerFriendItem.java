/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.friends;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.happydroids.droidtowers.gamestate.GameSave;
import com.happydroids.droidtowers.gamestate.server.CloudGameSave;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.VibrateClickListener;

public class NonPlayerFriendItem extends PlayerFriendItem {
  public final NonPlayerFriend profile;

  public NonPlayerFriendItem(NonPlayerFriend profile, CloudGameSave playerGameSave) {
    super(playerGameSave);
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

  @Override
  protected TextButton makeActionButton() {
    TextButton inviteButton = FontManager.Roboto18.makeTextButton("Invite to Play");
    inviteButton.setClickListener(new VibrateClickListener() {
      @Override
      public void onClick(Actor actor, float x, float y) {
        new InviteNonPlayerFriendWindow(profile).show();
      }
    });
    return inviteButton;
  }
}
