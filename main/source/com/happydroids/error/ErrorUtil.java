/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.error;

import com.happydroids.platform.Platform;

public class ErrorUtil {
  public static void rethrowError(Throwable throwable) {
    Platform.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
  }
}
