/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

public class GridObjectEvent {
  public final GridObject gridObject;

  public GridObjectEvent(GridObject gridObject) {
    if (gridObject == null) {
      throw new RuntimeException(this.getClass().getSimpleName() + " cannot be created with out a valid GridObject");
    }

    this.gridObject = gridObject;
  }
}
