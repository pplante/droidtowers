/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class FriendsListWindow extends ScrollableTowerWindow {

  public FriendsListWindow(Stage stage) {
    super("My Friends", stage);

    setStaticHeader(new ActionBar());

    for (int i = 0; i < 25; i++) {
      row();
      add(FontManager.Roboto64.makeLabel("Row #" + i));
    }
  }

}
