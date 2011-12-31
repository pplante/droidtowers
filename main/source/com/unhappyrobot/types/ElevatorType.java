package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;

public class ElevatorType extends GridObjectType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Elevator(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return true;
  }
}
