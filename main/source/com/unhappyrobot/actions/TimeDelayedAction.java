package com.unhappyrobot.actions;

public abstract class TimeDelayedAction implements Action {
  private final long updateFrequency;
  protected boolean shouldRepeat;
  private long nextTimeToRun;
  private boolean hasRunBefore;

  public TimeDelayedAction(long updateFrequency) {
    this(updateFrequency, true);
  }

  public TimeDelayedAction(long updateFrequency, boolean shouldRepeat) {
    this.updateFrequency = updateFrequency;
    this.shouldRepeat = shouldRepeat;

    resetInterval();
  }

  public void act(long currentTime) {
    if (hasRunBefore && !shouldRepeat) {
      return;
    }

    if (nextTimeToRun < currentTime) {
      nextTimeToRun = currentTime + updateFrequency;

      run();
    }
  }

  public void resetInterval() {
    nextTimeToRun = System.currentTimeMillis() + updateFrequency;
    hasRunBefore = false;
  }

  public abstract void run();
}
