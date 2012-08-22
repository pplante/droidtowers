/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;


import com.badlogic.gdx.Gdx;
import com.happydroids.HappyDroidConsts;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.badlogic.gdx.Application.ApplicationType.Android;

public abstract class HappyDroidUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
  protected StringBuilder generateExceptionErrorString(Throwable throwable) {
    StringBuilder message = new StringBuilder();
    message.append("Wow, terribly sorry about this, but an unknown error has occurred.\n\n");
    if (Gdx.app.getType().equals(Android)) {
      message.append("Would you mind if we sent some anonymous data to happydroids.com for analysis?\n\n");
    } else {
      message.append("Some anonymous data about this crash has been sent to happydroids.com for analysis.\n\n");
    }

    if (HappyDroidConsts.DEBUG) {
      message.append("\n\nERROR:\n\n");

      StringWriter writer = new StringWriter();
      throwable.printStackTrace(new PrintWriter(writer));
      message.append(writer.toString());
    }
    return message;
  }
}
