/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.error;

import com.happydroids.HappyDroidConsts;
import com.happydroids.platform.Platform;
import com.happydroids.utils.BackgroundTask;
import net.kencochrane.sentry.RavenClient;

public class ErrorUtil {
  public static void rethrowError(Throwable throwable) {
    Platform.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
  }

  public static void sendErrorToServer(final Throwable throwable) {
    throwable.printStackTrace();

    new BackgroundTask() {
      @Override
      protected void execute() throws Exception {
        RavenClient ravenClient = new RavenClient(HappyDroidConsts.SENTRY_DSN);
        ravenClient.captureException(throwable);
      }
    }.run();
  }
}
