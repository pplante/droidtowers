package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

import java.util.EventObject;

public class GridObjectAddedEvent extends EventObject {
  public GridObjectAddedEvent(Object o) {
    super(o);
  }

  @Override
  public GridObject getSource() {
    return (GridObject) super.getSource();
  }
}
