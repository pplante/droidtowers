package com.unhappyrobot.types;

import com.unhappyrobot.entities.Elevator;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Room;
import com.unhappyrobot.grid.GameGrid;

public class ElevatorType extends TransitType {
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
  public int getCoinsEarned() {
    return 0;
  }

  @Override
  public boolean connectsToFloor(GridObject gridObject, float floor) {
    return gridObject.getContentPosition().y <= floor && floor <= (gridObject.getContentPosition().y + gridObject.getContentSize().y);
  }
}
