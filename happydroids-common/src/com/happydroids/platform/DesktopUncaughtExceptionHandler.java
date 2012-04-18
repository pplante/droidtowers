/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.server.CrashReport;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DesktopUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  private static final String TAG = DesktopUncaughtExceptionHandler.class.getSimpleName();

  public void uncaughtException(Thread thread, final Throwable throwable) {
    Logger.getLogger(TAG).log(Level.SEVERE, "Uncaught exception!", throwable);

    new Thread() {
      @Override
      public void run() {
        new CrashReport(throwable).saveBlocking();
      }
    }.run();

    JOptionPane.showMessageDialog(null, generateExceptionErrorString(throwable), "Ooops!", JOptionPane.ERROR_MESSAGE);

    System.exit(100);
  }
}
