package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectRemovedEvent extends GridObjectEvent {
  public GridObjectRemovedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
