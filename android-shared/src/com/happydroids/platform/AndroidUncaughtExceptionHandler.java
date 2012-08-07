/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.badlogic.gdx.Gdx;
import com.happydroids.error.ErrorUtil;

public class AndroidUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  private static final String TAG = AndroidUncaughtExceptionHandler.class.getSimpleName();

  private final Activity activity;

  public AndroidUncaughtExceptionHandler(Activity activity) {
    this.activity = activity;
  }

  public void uncaughtException(Thread thread, final Throwable throwable) {
    Gdx.app.error(TAG, "Uncaught exception!", throwable);

    activity.runOnUiThread(new Runnable() {
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Oooops!")
                .setMessage(generateExceptionErrorString(throwable))
                .setPositiveButton("Send Report", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialogInterface, int i) {
                    ProgressDialog.Builder pb = new ProgressDialog.Builder(activity);
                    pb.setMessage("Sending crash report...");
                    pb.setCancelable(false);
                    pb.show();

                    if (Platform.getConnectionMonitor().isConnectedOrConnecting()) {
                      ErrorUtil.sendErrorToServer(throwable);
                    }
                    dialogInterface.dismiss();
                  }
                })
                .setNegativeButton("Just Exit", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                }).show()
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                  @Override
                  public void onDismiss(DialogInterface dialog) {
                    System.exit(0);
                  }
                });
      }
    });
  }
}
