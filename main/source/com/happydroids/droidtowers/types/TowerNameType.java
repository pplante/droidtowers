/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.grid.GameGrid;

public class TowerNameType extends GridObjectType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new TowerNameBillboard(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return true;
  }
}
