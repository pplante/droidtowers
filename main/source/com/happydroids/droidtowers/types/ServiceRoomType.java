/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.entities.GridObject;
import com.happydroids.droidtowers.entities.ServiceRoom;
import com.happydroids.droidtowers.grid.GameGrid;

public class ServiceRoomType extends CommercialType {
  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    return new ServiceRoom(this, gameGrid);
  }
}
