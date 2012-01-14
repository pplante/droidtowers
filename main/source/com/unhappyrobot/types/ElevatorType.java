package com.unhappyrobot.types;

import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Room;

public class ElevatorType extends GridObjectType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Elevator(this, gameGrid);
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Room;
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return checkForOverlap(gridObject);
  }

  @Override
  public int getZIndex() {
    return 100;
  }

  @Override
  public int getCoinsEarned() {
    return 0;
  }

}
