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

    setMessage("Sorry this feature requires you to login to happydroids.com first.\n\nWould you like to do so now?");

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
