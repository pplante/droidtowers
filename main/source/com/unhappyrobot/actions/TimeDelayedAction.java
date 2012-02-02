package com.unhappyrobot.actions;

public abstract class TimeDelayedAction implements Action {
  private final long updateFrequency;
  private long nextTimeToRun;
  private boolean shouldRepeat;
  private boolean hasRunBefore;
  private boolean paused;

  public TimeDelayedAction(long updateFrequency) {
    this(updateFrequency, true);
  }

  public TimeDelayedAction(long updateFrequency, boolean shouldRepeat) {
    this.updateFrequency = updateFrequency;
    this.shouldRepeat = shouldRepeat;
  }

  public void act(long currentTime) {
    if (hasRunBefore && !shouldRepeat) {
      return;
    } else if (paused) {
      return;
    }

    if (nextTimeToRun < currentTime) {
      nextTimeToRun = currentTime + updateFrequency;
      hasRunBefore = true;

      run();
    }
  }

  public void resetInterval() {
    nextTimeToRun = System.currentTimeMillis() + updateFrequency;
    hasRunBefore = false;
  }

  public boolean isPaused() {
    return paused;
  }

  public void pause() {
    paused = true;
  }

  public void unpause() {
    paused = false;
  }

  public abstract void run();
}
