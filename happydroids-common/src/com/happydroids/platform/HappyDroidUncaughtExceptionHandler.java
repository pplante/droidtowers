/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;


import com.happydroids.HappyDroidConsts;

public abstract class HappyDroidUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  protected StringBuilder generateExceptionErrorString(Throwable throwable) {
    StringBuilder message = new StringBuilder();
    message.append("Wow, terribly sorry about this, but an unknown error has occurred.\n\n");
    message.append("Some anonymous data about this crash has been sent to happydroids.com for analysis.\n\n");
    message.append("The game will now exit.");

    if (HappyDroidConsts.DEBUG) {
      message.append("\n\nERROR:\n\n");
      message.append(throwable.toString());
    }
    return message;
  }
}
