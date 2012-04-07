/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

public class GridObjectRemovedEvent extends GridObjectEvent {
  public GridObjectRemovedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
