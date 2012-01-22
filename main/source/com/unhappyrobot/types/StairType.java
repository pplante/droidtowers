package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Stair;

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
  public boolean coversFloor(GridObject gridObject, float floor) {
    return floor == gridObject.getPosition().y || floor == gridObject.getPosition().y + 1;
  }
}
