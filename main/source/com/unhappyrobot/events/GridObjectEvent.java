package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectEvent {
  public final GridObject gridObject;

  public GridObjectEvent(GridObject gridObject) {
    if (gridObject == null) {
      throw new RuntimeException(this.getClass().getSimpleName() + " cannot be created with out a valid GridObject");
    }

    this.gridObject = gridObject;
  }
}