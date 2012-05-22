/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriendCollection;
import com.happydroids.droidtowers.gamestate.server.PlayerFriendCollection;
import com.happydroids.droidtowers.gamestate.server.PlayerProfile;
import com.happydroids.droidtowers.gui.friends.NonPlayerFriendItem;
import com.happydroids.droidtowers.gui.friends.PlayerFriendItem;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apache.http.HttpResponse;

import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class FriendsListWindow extends ScrollableTowerWindow {

  private final TextureAtlas.AtlasRegion facebookIcon;
  private List<PlayerFriendItem> friendRows;
  private boolean friendsFetched;
  private boolean nonFriendsFetched;

  public FriendsListWindow(Stage stage) {
    super("My Friends", stage);

    facebookIcon = TowerAssetManager.textureFromAtlas("facebook-logo", "hud/menus.txt");
    friendRows = Lists.newArrayList();

    defaults().left().space(scale(6));

    NonPlayerFriendSearchBox friendSearchBox = new NonPlayerFriendSearchBox();
    setStaticHeader(friendSearchBox);


    new PlayerFriendCollection().fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<PlayerProfile>>() {
      @Override
      public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<PlayerProfile> collection) {
        friendsFetched = true;
        updateViewWhenFinished();
      }

      @Override
      public void onSuccess(HttpResponse response, HappyDroidServiceCollection<PlayerProfile> collection) {
        for (PlayerProfile profile : collection.getObjects()) {
          addPlayerFriendRow(new PlayerFriendItem(profile));
        }

        friendsFetched = true;
        updateViewWhenFinished();
      }
    });

    new NonPlayerFriendCollection().fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<NonPlayerFriend>>() {
      @Override
      public void onError(HttpResponse response, int statusCode, HappyDroidServiceCollection<NonPlayerFriend> collection) {
        nonFriendsFetched = true;
        updateViewWhenFinished();
      }

      @Override
      public void onSuccess(HttpResponse response, HappyDroidServiceCollection<NonPlayerFriend> collection) {
        for (NonPlayerFriend profile : collection.getObjects()) {
          addPlayerFriendRow(new NonPlayerFriendItem(profile));
        }

        nonFriendsFetched = true;
        updateViewWhenFinished();
      }
    });
  }

  private void updateViewWhenFinished() {
    if (!friendsFetched || !nonFriendsFetched) {
      return;
    }

    for (PlayerFriendItem friendRow : friendRows) {
      row().fillX();
      add(friendRow).expandX();
    }

    shoveContentUp();
    content.invalidateHierarchy();
  }

  private void addPlayerFriendRow(PlayerFriendItem playerFriendItem) {
    playerFriendItem.createChildren(facebookIcon);
    friendRows.add(playerFriendItem);
  }

  private class NonPlayerFriendSearchBox extends Table {
    private NonPlayerFriendSearchBox() {
      defaults().pad(scale(10));
      row().left();
      TextField searchField = FontManager.Roboto24.makeTextField("", "Search by name");
      add(searchField).width(400);
      add(FontManager.Roboto18.makeTextButton("Apply"));

      searchField.setTextFieldListener(new TextField.TextFieldListener() {
        @Override
        public void keyTyped(TextField textField, char key) {
          filterFriends(textField.getText().toLowerCase());
        }

        private void filterFriends(String text) {
          for (PlayerFriendItem friendRow : friendRows) {
            Cell friendCell = FriendsListWindow.this.content.getCell(friendRow);
            boolean nameMatches = friendRow.playerNameMatches(text);
            friendRow.visible = nameMatches;
            friendCell.ignore(!nameMatches);
          }

          FriendsListWindow.this.content.invalidateHierarchy();
        }
      });
    }
  }
}
