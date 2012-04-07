/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

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
