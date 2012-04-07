/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.actions;

public abstract class Action {
  protected boolean markedForRemoval;

  protected void markToRemove() {
    markedForRemoval = true;
  }

  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }

  public abstract void act(float deltaTime);
}
