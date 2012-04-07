/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

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
