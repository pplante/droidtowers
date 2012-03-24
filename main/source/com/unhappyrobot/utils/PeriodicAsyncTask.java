package com.unhappyrobot.utils;

import static java.lang.Thread.sleep;
import static java.lang.Thread.yield;

public abstract class PeriodicAsyncTask extends AsyncTask {
  private final long updateFrequency;

  protected PeriodicAsyncTask(long updateFrequency) {
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
