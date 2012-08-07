/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.happydroids.droidtowers.gui.ConnectToHappyDroidsWindow;
import com.happydroids.droidtowers.gui.Dialog;
import com.happydroids.droidtowers.gui.VibrateClickListener;

public class ConnectToHappyDroidsPrompt extends Dialog {
  public ConnectToHappyDroidsPrompt() {
    super();

    setTitle("View other towers in your neighborhood");
    setMessage("Viewing the towers made by your friends requires you to\nlogin via happydroids.com with a Facebook account.\n\nAfter doing so, you can see the towers that your\nfriends have been creating.\n\nWould you like to login now?");

    addButton("Sure", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        new ConnectToHappyDroidsWindow(getStage()).show();

        dismiss();
      }
    });

    addButton("No", new VibrateClickListener() {
      @Override
      public void onClick(InputEvent event, float x, float y) {
        dismiss();
      }
    });
  }
}
