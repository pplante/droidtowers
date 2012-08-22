/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.platform;

import org.acra.ACRA;

public class AndroidUncaughtExceptionHandler extends HappyDroidUncaughtExceptionHandler {
  public void uncaughtException(Thread thread, final Throwable throwable) {
    ACRA.getErrorReporter().uncaughtException(thread, throwable);
  }
}
