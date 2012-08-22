/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.GameState;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriendCollection;
import com.happydroids.droidtowers.gamestate.server.PlayerFriendCollection;
import com.happydroids.droidtowers.gamestate.server.PlayerProfile;
import com.happydroids.droidtowers.gui.friends.NonPlayerFriendItem;
import com.happydroids.droidtowers.gui.friends.PlayerFriendItem;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.server.HappyDroidServiceCollection;
import com.happydroids.utils.BackgroundTask;

import java.util.List;

public class FriendsListWindow extends ScrollableTowerWindow {

  private final TextureRegionDrawable facebookIcon;
  private List<PlayerFriendItem> playerFriendRows;
  private List<PlayerFriendItem> nonPlayerFriendRows;
  private boolean playerFriendsFetched;
  private boolean nonPlayerFriendsFetched;
  private final GameState playerGameState;

  public FriendsListWindow(Stage stage, GameState gameState) {
    super("My Friends", stage);
    playerGameState = gameState;

    facebookIcon = TowerAssetManager.drawableFromAtlas("facebook-logo", "hud/menus.txt");
    playerFriendRows = Lists.newArrayList();
    nonPlayerFriendRows = Lists.newArrayList();

    defaults().left().space(Display.devicePixel(6));

    add(FontManager.Roboto32.makeLabel("making friends :]"));

    NonPlayerFriendSearchBox friendSearchBox = new NonPlayerFriendSearchBox(this);
    setStaticHeader(friendSearchBox);

    new BackgroundTask() {
      private PlayerFriendCollection friendCollection;

      @Override
      protected void execute() throws Exception {
        friendCollection = new PlayerFriendCollection();
        friendCollection.fetch();
      }

      @Override
      public synchronized void afterExecute() {
        processPlayerFriends(friendCollection);
      }
    }.run();

    new BackgroundTask() {
      private NonPlayerFriendCollection friendCollection;

      @Override
      protected void execute() throws Exception {
        friendCollection = new NonPlayerFriendCollection();
        friendCollection.fetch();
      }

      @Override
      public synchronized void afterExecute() {
        processNonPlayerFriends(friendCollection);
      }
    }.run();
  }

  private void processNonPlayerFriends(HappyDroidServiceCollection<NonPlayerFriend> collection) {
    if (collection != null && !collection.isEmpty()) {
      for (NonPlayerFriend profile : collection.getObjects()) {
        PlayerFriendItem playerFriendItem = new NonPlayerFriendItem(profile, playerGameState);
        playerFriendItem.createChildren(facebookIcon);
        nonPlayerFriendRows.add(playerFriendItem);
      }
    }

    nonPlayerFriendsFetched = true;
    updateViewWhenFinished();
  }

  private void processPlayerFriends(HappyDroidServiceCollection<PlayerProfile> collection) {
    playerFriendsFetched = true;

    if (collection != null && !collection.isEmpty()) {
      for (PlayerProfile profile : collection.getObjects()) {
        PlayerFriendItem playerFriendItem = new PlayerFriendItem(profile, playerGameState);
        playerFriendItem.createChildren(facebookIcon);
        playerFriendRows.add(playerFriendItem);
      }
    }

    updateViewWhenFinished();
  }

  private void updateViewWhenFinished() {
    if (!playerFriendsFetched || !nonPlayerFriendsFetched) {
      return;
    }

    clear();

    row().fillX();
    add(FontManager.Roboto24.makeLabel("Friends playing Droid Towers")).expandX();
    row().fillX();
    add(new HorizontalRule()).expandX();


    if (!playerFriendRows.isEmpty()) {
      for (PlayerFriendItem friendRow : playerFriendRows) {
        row().fillX();
        add(friendRow).expandX();
      }
    } else {
      row().fillX();
      add(FontManager.Roboto18.makeLabel("You should invite some of your friends to play with.")).expandX();
    }

    row().fillX().padTop(Display.devicePixel(32));
    add(FontManager.Roboto24.makeLabel("Friends on Facebook")).expandX();
    row().fillX();
    add(new HorizontalRule()).expandX();
    if (!nonPlayerFriendRows.isEmpty()) {
      for (PlayerFriendItem friendRow : nonPlayerFriendRows) {
        row().fillX();
        add(friendRow).expandX();
      }
    } else {
      row().fillX();
      if (playerFriendRows.isEmpty()) {
        add(FontManager.Roboto18
                    .makeLabel("Wow, terribly sorry to tell you this..\n\nBut you appear to have no friends.\n\n")).expandX();
      } else {
        add(FontManager.Roboto18.makeLabel("You have already invited everyone, thanks!")).expandX();
      }
    }

    shoveContentUp();
    content.invalidateHierarchy();
  }

  public List<PlayerFriendItem> getNonPlayerFriendRows() {
    return nonPlayerFriendRows;
  }

  public List<PlayerFriendItem> getPlayerFriendRows() {
    return playerFriendRows;
  }
}
