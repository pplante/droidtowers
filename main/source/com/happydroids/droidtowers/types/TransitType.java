/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class TransitType extends GridObjectType {
  @Override
  public int getZIndex() {
    return 90;
  }

  public abstract boolean connectsToFloor(GridObject gridObject, float floor);
}
