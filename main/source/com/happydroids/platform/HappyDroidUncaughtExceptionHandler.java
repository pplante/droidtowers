/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;


import com.happydroids.HappyDroidConsts;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class HappyDroidUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  protected StringBuilder generateExceptionErrorString(Throwable throwable) {
    StringBuilder message = new StringBuilder();
    message.append("Wow, terribly sorry about this, but an unknown error has occurred.\n\n");
    message.append("Some anonymous data about this crash has been sent to happydroids.com for analysis.\n\n");
    message.append("The game will now exit.");

    if (HappyDroidConsts.DEBUG) {
      message.append("\n\nERROR:\n\n");

      StringWriter writer = new StringWriter();
      throwable.printStackTrace(new PrintWriter(writer));
      message.append(writer.toString());
    }
    return message;
  }
}
