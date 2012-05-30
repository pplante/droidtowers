/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.happydroids.droidtowers.gui.friends.PlayerFriendItem;

import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import static com.happydroids.droidtowers.platform.Display.scale;

class NonPlayerFriendSearchBox extends Table {
  NonPlayerFriendSearchBox(final FriendsListWindow friendsListWindow) {
    TextField searchField = FontManager.Roboto24.makeTextField("", "Filter friends by name");

    defaults().pad(scale(10));
    row().right().fillX();
    add(searchField).width(scale(400)).right().expandX();
    add(FontManager.Roboto18.makeTextButton("Apply")).padRight(scale(24));

    searchField.setTextFieldListener(new TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char key) {
        filterFriends(textField.getText().toLowerCase());
      }

      private void filterFriends(String text) {
        for (PlayerFriendItem friendRow : friendsListWindow.getPlayerFriendRows()) {
          Cell friendCell = friendsListWindow.content.getCell(friendRow);
          boolean nameMatches = friendRow.playerNameMatches(text);
          friendRow.visible = nameMatches;
          friendCell.ignore(!nameMatches);
        }

        for (PlayerFriendItem friendRow : friendsListWindow.getNonPlayerFriendRows()) {
          Cell friendCell = friendsListWindow.content.getCell(friendRow);
          boolean nameMatches = friendRow.playerNameMatches(text);
          friendRow.visible = nameMatches;
          friendCell.ignore(!nameMatches);
        }

        friendsListWindow.content.invalidateHierarchy();
      }
    });
  }
}
