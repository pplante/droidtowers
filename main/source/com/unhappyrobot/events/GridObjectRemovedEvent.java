package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectRemovedEvent {
  public final GridObject gridObject;

  public GridObjectRemovedEvent(GridObject gridObject) {
    this.gridObject = gridObject;
  }
}
