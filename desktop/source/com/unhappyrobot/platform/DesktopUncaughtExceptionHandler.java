/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.platform;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.gamestate.server.CrashReport;

import javax.swing.*;

public class DesktopUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  private static final String TAG = DesktopUncaughtExceptionHandler.class.getSimpleName();

  public void uncaughtException(Thread thread, Throwable throwable) {
    new CrashReport(throwable).save();
    Gdx.app.error(TAG, "Uncaught exception!", throwable);

    JOptionPane.showMessageDialog(null, generateExceptionErrorString(throwable), "Ooops!", JOptionPane.ERROR_MESSAGE);

    System.exit(100);
  }
}
