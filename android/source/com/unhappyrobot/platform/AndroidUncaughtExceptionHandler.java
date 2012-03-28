package com.unhappyrobot.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.unhappyrobot.gamestate.server.CrashReport;
import com.unhappyrobot.utils.BackgroundTask;

public class AndroidUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  private final Activity activity;

  public AndroidUncaughtExceptionHandler(Activity activity) {
    this.activity = activity;
  }

  public void uncaughtException(Thread thread, final Throwable throwable) {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        new BackgroundTask() {
          @Override
          public void execute() {
            new CrashReport(throwable).save();
          }
        }.run();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Oooops!")
                .setMessage("Wow, terribly sorry about this, but an unknown error has occurred.\n\nSome anonymous data about this crash has been sent to happydroids.com for analysis.\n\nThe game will now exit.")
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialogInterface, int i) {
                    System.exit(100);
                  }
                }).show();
      }
    });
  }
}
