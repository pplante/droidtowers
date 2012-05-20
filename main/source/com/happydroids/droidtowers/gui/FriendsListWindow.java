/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.collect.Lists;
import com.happydroids.droidtowers.TowerAssetManager;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriend;
import com.happydroids.droidtowers.gamestate.server.NonPlayerFriendCollection;
import com.happydroids.server.ApiCollectionRunnable;
import com.happydroids.server.HappyDroidServiceCollection;
import org.apache.http.HttpResponse;

import java.util.List;

import static com.happydroids.droidtowers.platform.Display.scale;

public class FriendsListWindow extends ScrollableTowerWindow {

  private final TextureAtlas.AtlasRegion facebookIcon;
  private List<NonPlayerFriendItem> friendRows;

  public FriendsListWindow(Stage stage) {
    super("My Friends", stage);

    facebookIcon = TowerAssetManager.textureFromAtlas("facebook-logo", "hud/menus.txt");

    defaults().left().space(scale(6));

    NonPlayerFriendSearchBox friendSearchBox = new NonPlayerFriendSearchBox();
    setStaticHeader(friendSearchBox);

    friendRows = Lists.newArrayList();

    new NonPlayerFriendCollection().fetch(new ApiCollectionRunnable<HappyDroidServiceCollection<NonPlayerFriend>>() {
      @Override
      public void onSuccess(HttpResponse response, HappyDroidServiceCollection<NonPlayerFriend> collection) {
        for (NonPlayerFriend profile : collection.getObjects()) {
          row().fill();
          NonPlayerFriendItem friendItem = new NonPlayerFriendItem(profile);
          add(friendItem).expandX();

          friendRows.add(friendItem);
        }

        shoveContentUp();
      }
    });
  }

  private class NonPlayerFriendItem extends Table {
    public final NonPlayerFriend profile;

    public NonPlayerFriendItem(NonPlayerFriend profile) {
      this.profile = profile;
      defaults().pad(scale(4));

      row().fill();
      add(new Image(facebookIcon, Scaling.none)).spaceRight(scale(10));
      add(FontManager.Roboto18.makeLabel(profile.getFriendName())).expandX();

      row().fill();
      add(new HorizontalRule(1)).colspan(2);
    }
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
          for (NonPlayerFriendItem friendRow : friendRows) {
            Cell friendCell = FriendsListWindow.this.content.getCell(friendRow);
            boolean nameMatches = friendRow.profile.getFriendName().toLowerCase().startsWith(text);
            friendRow.visible = nameMatches;
            friendCell.ignore(!nameMatches);
          }

          FriendsListWindow.this.content.invalidateHierarchy();
        }
      });
    }
  }
}
