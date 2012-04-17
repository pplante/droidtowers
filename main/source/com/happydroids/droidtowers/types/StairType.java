/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Stair;
import com.happydroids.droidtowers.grid.GameGrid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StairType extends TransitType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Stair(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return checkForOverlap(gridObject);
  }

  @Override
  public int getCoinsEarned() {
    return 0;
  }

  @Override
  public boolean connectsToFloor(GridObject gridObject, float floor) {
    return floor == gridObject.getPosition().y || floor == gridObject.getPosition().y + 1;
  }

  @Override
  public int getZIndex() {
    return 80;
  }
}
