package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectChangedEvent {
  public final GridObject gridObject;
  public final String nameOfParamChanged;

  public GridObjectChangedEvent(GridObject gridObject, String nameOfParamChanged) {
    this.gridObject = gridObject;
    this.nameOfParamChanged = nameOfParamChanged;
  }
}
