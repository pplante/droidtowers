package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

import java.util.EventObject;

public class GridObjectChangedEvent extends EventObject {
  public GridObjectChangedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
