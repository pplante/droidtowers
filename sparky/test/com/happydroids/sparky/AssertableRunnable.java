/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.sparky;

public class AssertableRunnable implements Runnable {
  private boolean wasCalled = false;

  public void run() {
    wasCalled = true;
  }

  public boolean hasBeenCalled() {
    return wasCalled;
  }
}
