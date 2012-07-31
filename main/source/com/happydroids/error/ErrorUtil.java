/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.error;

import com.happydroids.platform.Platform;
import com.happydroids.server.CrashReport;
import com.happydroids.utils.BackgroundTask;

public class ErrorUtil {
  public static void rethrowError(Throwable throwable) {
    Platform.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
  }

  public static void sendErrorToServer(final Throwable throwable) {
    new BackgroundTask() {
      @Override
      protected void execute() throws Exception {
        new CrashReport(throwable).save();
      }
    }.run();
  }
}
