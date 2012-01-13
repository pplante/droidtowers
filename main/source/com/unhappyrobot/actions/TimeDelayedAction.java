package com.unhappyrobot.actions;

public abstract class TimeDelayedAction implements Action {
  private final long updateFrequency;
  private long lastRunTime;

  public TimeDelayedAction(long updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  public void call(long currentTime) {
    if (lastRunTime + updateFrequency < currentTime) {
      lastRunTime = currentTime;

      run();
    }
  }

  public abstract void run();
}
