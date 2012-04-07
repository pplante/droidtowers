/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

public class GridObjectAddedEvent extends GridObjectEvent {
  public GridObjectAddedEvent(GridObject gridObject) {
    super(gridObject);
  }
}
