package com.unhappyrobot.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.badlogic.gdx.Gdx;
import com.unhappyrobot.gamestate.server.CrashReport;
import com.unhappyrobot.utils.BackgroundTask;

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
          public void execute() {
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
