package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectAddedEvent {
  public final GridObject gridObject;

  public GridObjectAddedEvent(GridObject gridObject) {
    if (gridObject == null) {
      throw new RuntimeException("GridObjectAddedEvent cannot be created with out a valid GridObject");
    }

    this.gridObject = gridObject;
  }
}
