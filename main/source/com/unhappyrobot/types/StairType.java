package com.unhappyrobot.types;

import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GridObject;
import com.unhappyrobot.entities.Stair;

public class StairType extends GridObjectType {

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Stair(this, gameGrid);
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    return checkForOverlap(gridObject);
  }

  @Override
  public int getZIndex() {
    return 90;
  }

  @Override
  public int getCoinsEarned() {
    return 0;
  }

  @Override
  public int getGoldEarned() {
    return 0;
  }
}
