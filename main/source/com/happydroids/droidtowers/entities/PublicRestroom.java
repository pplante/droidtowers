/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.ServiceRoomType;

public class PublicRestroom extends ServiceRoom {
  public PublicRestroom(ServiceRoomType serviceRoomType, GameGrid gameGrid) {
    super(serviceRoomType, gameGrid);
  }

  @Override
  protected boolean canEmployDroids() {
    return false;
  }

  @Override public boolean needsDroids() {
    return false;
  }

  @Override
  public boolean canEarnMoney() {
    return false;
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return null;
  }
}
