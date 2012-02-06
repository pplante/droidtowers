package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectChangedEvent extends GridObjectEvent {
  public final String nameOfParamChanged;

  public GridObjectChangedEvent(GridObject gridObject, String nameOfParamChanged) {
    super(gridObject);

    this.nameOfParamChanged = nameOfParamChanged;
  }
}
