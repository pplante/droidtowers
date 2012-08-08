/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.input;

import com.badlogic.gdx.InputProcessor;

class InputProcessorEntry implements Comparable<InputProcessorEntry> {
  private InputProcessor inputProcessor;
  private int priority;
  private boolean markedForRemoval;


  public InputProcessorEntry(InputProcessor inputProcessor, int priority) {
    this.inputProcessor = inputProcessor;
    this.priority = priority;
  }

  public InputProcessor getInputProcessor() {
    return inputProcessor;
  }

  public int getPriority() {
    return priority;
  }

  public void markForRemoval() {
    markedForRemoval = true;
  }

  public boolean isMarkedForRemoval() {
    return markedForRemoval;
  }

  @Override
  public int compareTo(InputProcessorEntry other) {
    return getPriority() > other.getPriority() ? 1 : -1;
  }
}
