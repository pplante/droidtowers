package com.unhappyrobot.actions;

public abstract class TimeDelayedAction implements Action {
  private float currentTime;
  private final float updateFrequency;
  private boolean shouldRepeat;
  private boolean hasRunBefore;
  private boolean paused;

  public TimeDelayedAction(long updateFrequency) {
    this(updateFrequency, true);
  }

  public TimeDelayedAction(float updateFrequency, boolean shouldRepeat) {
    this.updateFrequency = updateFrequency;
    currentTime = updateFrequency;
    this.shouldRepeat = shouldRepeat;
  }

  public void act(float deltaTime) {
    if (hasRunBefore && !shouldRepeat) {
      return;
    } else if (paused) {
      return;
    }

    currentTime += deltaTime;

    if (currentTime >= updateFrequency) {
      currentTime = 0.0f;
      hasRunBefore = true;

      run();
    }
  }

  public void resetInterval() {
    currentTime = 0.0f;
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
