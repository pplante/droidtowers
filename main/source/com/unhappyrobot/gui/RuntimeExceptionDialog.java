package com.unhappyrobot.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class RuntimeExceptionDialog extends Dialog {
  public RuntimeExceptionDialog(Stage stage, RuntimeException error) {
    super(stage);

    setTitle("An unexpected error occurred!");
    setMessage("Sorry, but something has gone wrong.\nSome anonymous data detailing the error has been sent to happydroids for analysis.\n\n" + error.toString());
    addButton(ResponseType.POSITIVE, "Dismiss", new OnClickCallback() {
      @Override
      public void onClick(Dialog dialog) {
        dialog.dismiss();
      }
    });
  }
}
