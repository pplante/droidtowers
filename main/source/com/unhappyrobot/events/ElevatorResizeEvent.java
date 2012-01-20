package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

public class ElevatorResizeEvent {
  public final GridObject gridObject;
  public final GridPoint prevSize;
  public final GridPoint prevPosition;

  public ElevatorResizeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    if (gridObject == null) {
      throw new RuntimeException("ElevatorResizeEvent cannot be created with out a valid GridObject");
    }

    this.gridObject = gridObject;
    this.prevSize = prevSize;
    this.prevPosition = prevPosition;
  }
}
