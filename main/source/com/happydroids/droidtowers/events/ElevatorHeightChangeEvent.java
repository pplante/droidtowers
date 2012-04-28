/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.events;

import com.happydroids.droidtowers.entities.GridObject;

public class ElevatorHeightChangeEvent extends GridObjectEvent {
  public ElevatorHeightChangeEvent(GridObject gridObject) {
    super(gridObject);
  }
}
