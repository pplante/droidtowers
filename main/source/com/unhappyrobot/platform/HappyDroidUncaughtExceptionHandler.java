package com.unhappyrobot.platform;

import com.unhappyrobot.TowerConsts;

public abstract class HappyDroidUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  protected StringBuilder generateExceptionErrorString(Throwable throwable) {
    StringBuilder message = new StringBuilder();
    message.append("Wow, terribly sorry about this, but an unknown error has occurred.\n\n");
    message.append("Some anonymous data about this crash has been sent to happydroids.com for analysis.\n\n");
    message.append("The game will now exit.");

    if (TowerConsts.DEBUG) {
      message.append("\n\nERROR:\n\n");
      message.append(throwable.toString());
    }
    return message;
  }
}
