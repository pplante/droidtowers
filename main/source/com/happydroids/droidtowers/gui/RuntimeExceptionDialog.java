/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.DroidTowersGame;

public class RuntimeExceptionDialog extends Dialog {
  private static final String TAG = RuntimeExceptionDialog.class.getSimpleName();

  public RuntimeExceptionDialog(Throwable error) {
    this(DroidTowersGame.getRootUiStage(), error);
  }

  public RuntimeExceptionDialog(Stage stage, Throwable error) {
    super(stage);

    setTitle("An unexpected error occurred!");

    String message = "Sorry, but something has gone wrong.\nSome anonymous data detailing the error has been sent to happydroids for analysis.\n\n";
    if (HappyDroidConsts.DEBUG) {
      message += error;
    }

    setMessage(message);
    addButton("Dismiss", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    });

    Gdx.app.error(TAG, "Uncaught Exception!", error);
  }
}
