/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.entities;

import com.badlogic.gdx.math.MathUtils;
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

  @Override public boolean needsDroids() {
    return false;
  }

  @Override
  public int getCoinsEarned() {
    if (isConnectedToTransport()) {
      return (int) MathUtils.clamp(gridObjectType.getCoinsEarned() * 0.25f * getNumVisitors(), 0, 3600);
    }

    return 0;
  }

  @Override
  public int getUpkeepCost() {
    return 0;
  }
}
