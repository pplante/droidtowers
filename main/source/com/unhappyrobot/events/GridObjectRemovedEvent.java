package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

import java.util.EventObject;

public class GridObjectRemovedEvent extends EventObject {
  public GridObjectRemovedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
