/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AndroidDialogOpener implements PlatformDialogOpener {
  private Activity activity;

  public AndroidDialogOpener(Activity activity) {
    this.activity = activity;
  }

  @Override
  public void showAlert(final String title, final String message) {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                })
                .show();
      }
    });
  }
}
