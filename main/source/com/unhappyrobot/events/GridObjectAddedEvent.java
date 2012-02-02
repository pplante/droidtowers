package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectAddedEvent extends GridObjectEvent {
  public GridObjectAddedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
