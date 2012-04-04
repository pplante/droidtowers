/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.input;

import com.badlogic.gdx.InputProcessor;

class InputProcessorEntry {
  private InputProcessor inputProcessor;
  private int priority;

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
}
