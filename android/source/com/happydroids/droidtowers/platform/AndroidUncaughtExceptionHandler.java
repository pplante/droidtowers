/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.badlogic.gdx.Gdx;
import com.happydroids.platform.HappyDroidUncaughtExceptionHandler;
import com.happydroids.server.CrashReport;
import com.happydroids.utils.BackgroundTask;

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
        new BackgroundTask() {
          @Override
          protected void execute() throws Exception {
            new CrashReport(throwable).save();
          }
        }.run();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Oooops!")
                .setMessage(generateExceptionErrorString(throwable))
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialogInterface, int i) {
                    System.exit(100);
                  }
                }).show();
      }
    });
  }
}
