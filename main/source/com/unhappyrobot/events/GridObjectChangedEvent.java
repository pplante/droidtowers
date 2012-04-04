/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;

public class GridObjectChangedEvent extends GridObjectEvent {
  public final String nameOfParamChanged;

  public GridObjectChangedEvent(GridObject gridObject, String nameOfParamChanged) {
    super(gridObject);

    this.nameOfParamChanged = nameOfParamChanged;
  }

  @Override
  public String toString() {
    return "GridObjectChangedEvent{" +
                   "nameOfParamChanged='" + nameOfParamChanged + '\'' +
                   '}';
  }
}
