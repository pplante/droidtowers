/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.unhappyrobot.events;

import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.math.GridPoint;

public class GridObjectBoundsChangeEvent extends GridObjectEvent {
  public final GridPoint prevSize;
  public final GridPoint prevPosition;

  public GridObjectBoundsChangeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    super(gridObject);

    this.prevSize = prevSize;
    this.prevPosition = prevPosition;
  }
}
