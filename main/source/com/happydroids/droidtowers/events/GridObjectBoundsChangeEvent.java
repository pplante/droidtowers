/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;

public class GridObjectBoundsChangeEvent extends GridObjectEvent {
  public final GridPoint prevSize;
  public final GridPoint prevPosition;

  public GridObjectBoundsChangeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    super(gridObject);

    this.prevSize = prevSize;
    this.prevPosition = prevPosition;
  }
}
