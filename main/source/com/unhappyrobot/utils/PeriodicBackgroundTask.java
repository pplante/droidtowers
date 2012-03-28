package com.unhappyrobot.utils;

import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;

public abstract class PeriodicBackgroundTask extends BackgroundTask {
  private final long updateFrequency;

  protected PeriodicBackgroundTask(long updateFrequency) {
    super();

    this.updateFrequency = updateFrequency;
  }

  @Override
  public final void execute() {
    while (update()) {
      try {
        System.out.println("updateFrequency = " + updateFrequency);
        sleep(updateFrequency);
        yield();
      } catch (InterruptedException ignored) {
      }
    }
  }

  public abstract boolean update();

  protected void cancel() {
    thread.interrupt();
  }
}
