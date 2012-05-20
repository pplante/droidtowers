/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class FriendsListWindow extends ScrollableTowerWindow {

  public FriendsListWindow(Stage stage) {
    super("My Friends", stage);

    setActionBar(new ActionBar());
  }

}
