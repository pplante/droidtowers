/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.types.ServiceRoomType;

public class ServiceRoom extends CommercialSpace {
  public ServiceRoom(ServiceRoomType serviceRoomType, GameGrid gameGrid) {
    super(serviceRoomType, gameGrid);
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new GridObjectPopOver(this);
  }

  @Override
  public int getCoinsEarned() {
    return 0;
  }
}
