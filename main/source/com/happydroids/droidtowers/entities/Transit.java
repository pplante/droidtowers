/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.types.GridObjectType;
import com.happydroids.droidtowers.types.TransitType;

public abstract class Transit extends GridObject {
  public Transit(GridObjectType gridObjectType, GameGrid gameGrid) {
    super(gridObjectType, gameGrid);
  }

  public boolean connectsToFloor(float floor) {
    return ((TransitType) getGridObjectType()).connectsToFloor(this, floor);
  }

  @Override
  public float getNormalizedCrimeLevel() {
    return 0f;
  }

  @Override
  public float getSurroundingCrimeLevel() {
    return 0f;
  }

  @Override
  public boolean canEarnMoney() {
    return false;
  }
}
