package com.unhappyrobot.entities;

import com.unhappyrobot.grid.GameGrid;
import com.unhappyrobot.types.GridObjectType;
import com.unhappyrobot.types.TransitType;

public abstract class Transit extends GridObject {
  public Transit(GridObjectType gridObjectType, GameGrid gameGrid) {
    super(gridObjectType, gameGrid);
  }

  public boolean connectsToFloor(float floor) {
    return ((TransitType) getGridObjectType()).connectsToFloor(this, floor);
  }
}
