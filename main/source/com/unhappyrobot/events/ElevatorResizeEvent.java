package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

public class ElevatorResizeEvent extends GridObjectEvent {
  public final GridPoint prevSize;
  public final GridPoint prevPosition;

  public ElevatorResizeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    super(gridObject);

    this.prevSize = prevSize;
    this.prevPosition = prevPosition;
  }
}
