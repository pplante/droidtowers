package com.unhappyrobot.entities;

import com.unhappyrobot.types.GridObjectType;
import com.unhappyrobot.types.TransitType;

public abstract class TransitGridObject extends GridObject {
  public TransitGridObject(GridObjectType gridObjectType, GameGrid gameGrid) {
    super(gridObjectType, gameGrid);
  }

  public boolean connectsToFloor(float floor) {
    return ((TransitType) getGridObjectType()).coversFloor(this, floor);
  }
}
