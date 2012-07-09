/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.gui.GridObjectPopOver;
import com.happydroids.droidtowers.gui.HotelRoomPopOver;
import com.happydroids.droidtowers.types.CommercialType;

public class HotelRoom extends CommercialSpace {
  public HotelRoom(CommercialType commercialType, GameGrid gameGrid) {
    super(commercialType, gameGrid);
  }

  @Override
  public GridObjectPopOver makePopOver() {
    return new HotelRoomPopOver(this);
  }

  @Override
  protected boolean canEmployDroids() {
    return false;
  }
}
