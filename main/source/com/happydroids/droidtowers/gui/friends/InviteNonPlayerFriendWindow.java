/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.friends;

import com.happydroids.droidtowers.TowerGame;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.TowerWindow;

import static com.happydroids.droidtowers.platform.Display.scale;

public class InviteNonPlayerFriendWindow extends TowerWindow {
  public InviteNonPlayerFriendWindow(NonPlayerFriend profile) {
    super("Invite " + profile.getFriendName() + " to Droid Towers!", TowerGame.getActiveScene().getStage());

    String inviteText = new StringBuilder()
                                .append("Hey ").append(profile.getFirstName()).append(",\n\n")
                                .append("I would like you to become my neighbor in Droid Towers, ")
                                .append("a really fun game I have been playing recently.")
                                .append("\n")
                                .append("<INVITATION LINK> to become my neighbor.")
                                .append("\n")
                                .append("\n")
                                .append("Best,\n")
                                .append(profile.getFriendName()).toString();

    add(FontManager.Roboto18.makeLabel("Message that will be sent to: " + profile.getFriendName())).expandX();
    row().fillX();
    add(FontManager.Roboto18.makeLabel(inviteText)).height(scale(250)).expandX();
  }
}
