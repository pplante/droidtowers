/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.utils;

import com.happydroids.HappyDroidConsts;
import com.happydroids.utils.BackgroundTask;

import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;

public abstract class PeriodicBackgroundTask extends BackgroundTask {
  private final long updateFrequency;
  private boolean canceled;

  protected PeriodicBackgroundTask(long updateFrequency) {
    super();

    this.updateFrequency = updateFrequency;
  }

  @Override
  protected final void execute() throws Exception {
    while (update() && !canceled) {
      try {
        if (HappyDroidConsts.DEBUG) {
          System.out.println("updateFrequency = " + updateFrequency);
        }
        sleep(updateFrequency);
        yield();
      } catch (InterruptedException ignored) {
      }
    }
  }

  public abstract boolean update();

  public synchronized void cancel() {
    canceled = true;
  }
}
