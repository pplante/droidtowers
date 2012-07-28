/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.input.GestureTool;

public class SwitchToolEvent {
  public final GestureTool selectedTool;

  public SwitchToolEvent(GestureTool selectedTool) {
    this.selectedTool = selectedTool;
  }
}
