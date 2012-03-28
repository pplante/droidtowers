package com.unhappyrobot.platform;

import com.badlogic.gdx.Gdx;
import com.unhappyrobot.gamestate.server.CrashReport;

import javax.swing.*;

public class DesktopUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  private static final String TAG = DesktopUncaughtExceptionHandler.class.getSimpleName();

  public void uncaughtException(Thread thread, Throwable throwable) {
    new CrashReport(throwable).save();
    Gdx.app.error(TAG, "Uncaught exception!", throwable);
    JOptionPane.showMessageDialog(null, "Wow, terribly sorry about this, but an unknown error has occurred.\n\nSome anonymous data about this crash has been sent to happydroids.com for analysis.\n\nThe game will now exit.", "Ooops!", JOptionPane.ERROR_MESSAGE);

    System.exit(100);
  }
}
