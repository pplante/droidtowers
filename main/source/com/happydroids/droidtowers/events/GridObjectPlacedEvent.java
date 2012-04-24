/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

public class GridObjectPlacedEvent extends GridObjectEvent {
  public GridObjectPlacedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
