/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.math.GridPoint;

public class ElevatorHeightChangeEvent extends GridObjectBoundsChangeEvent {
  public ElevatorHeightChangeEvent(GridObject gridObject, GridPoint prevSize, GridPoint prevPosition) {
    super(gridObject, prevSize, prevPosition);
  }
}
