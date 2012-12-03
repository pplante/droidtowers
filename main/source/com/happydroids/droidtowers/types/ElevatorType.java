/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.entities.Elevator;
import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.Room;
import com.happydroids.droidtowers.grid.GameGrid;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ElevatorType extends TransitType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new Elevator(this, gameGrid);
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Room;
  }

  @Override public boolean allowContinuousPurchase() {
    return false;
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
    return gridObject.getPosition().y + 1 <= floor && floor <= (gridObject.getPosition().y + 1 + gridObject.getSize().y - 2);
  }
}
