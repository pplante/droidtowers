/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.utils;

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
  public final void execute() {
    while (update() && !canceled) {
      try {
        System.out.println("updateFrequency = " + updateFrequency);
        sleep(updateFrequency);
        yield();
      } catch (InterruptedException ignored) {
      }
    }
  }

  public abstract boolean update();

  public void cancel() {
    canceled = true;
  }
}
