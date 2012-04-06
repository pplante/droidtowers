/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import com.happydroids.server.CrashReport;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HappyDroidsDesktopUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  private static final String TAG = HappyDroidsDesktopUncaughtExceptionHandler.class.getSimpleName();

  public void uncaughtException(Thread thread, Throwable throwable) {
    Logger.getLogger(TAG).log(Level.SEVERE, "Uncaught exception!", throwable);
    new CrashReport(throwable).save();

    JOptionPane.showMessageDialog(null, generateExceptionErrorString(throwable), "Ooops!", JOptionPane.ERROR_MESSAGE);

    System.exit(100);
  }
}
