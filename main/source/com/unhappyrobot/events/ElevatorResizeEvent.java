package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

import java.util.EventObject;

public class ElevatorResizeEvent extends EventObject {
  public final GridPoint prevSize;
  public final GridPoint prevPosition;

  public ElevatorResizeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    super(gridObject);
    this.prevSize = prevSize;
    this.prevPosition = prevPosition;
  }
}
