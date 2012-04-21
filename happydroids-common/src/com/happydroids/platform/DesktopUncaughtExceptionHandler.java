/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.server.ApiRunnable;
import com.happydroids.server.CrashReport;
import com.happydroids.server.HappyDroidServiceObject;
import org.apache.http.HttpResponse;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DesktopUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  private static final String TAG = DesktopUncaughtExceptionHandler.class.getSimpleName();

  public void uncaughtException(Thread thread, final Throwable throwable) {
    Logger.getLogger(TAG).log(Level.SEVERE, "Uncaught exception!", throwable);

    JOptionPane.showMessageDialog(null, generateExceptionErrorString(throwable), "Ooops!", JOptionPane.ERROR_MESSAGE);

    new CrashReport(throwable).save(new ApiRunnable() {
      @Override
      public void onError(HttpResponse response, int statusCode, HappyDroidServiceObject object) {
        System.out.println(response);
        System.exit(102);
      }

      @Override
      public void onSuccess(HttpResponse response, HappyDroidServiceObject object) {
        System.out.println(response);
        System.exit(101);
      }
    });
  }
}
