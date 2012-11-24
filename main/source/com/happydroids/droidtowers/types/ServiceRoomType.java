/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.grid.GameGrid;

public class ServiceRoomType extends CommercialType {

  public ServiceRoomType() {
    statsLine = null;
  }

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    if (this.provides(ProviderType.RESTROOM)) {
      return new PublicRestroom(this, gameGrid);
    } else if (this.provides(ProviderType.PARKING_RAMP)) {
      return new ParkingGarageRamp(this, gameGrid);
    } else if (this.provides(ProviderType.PARKING_SPACE)) {
      return new ParkingGarageSpace(this, gameGrid);
    }

    return new ServiceRoom(this, gameGrid);
  }

  @Override public boolean canBeAt(GridObject gridObject) {
    boolean superAllows = super.canBeAt(gridObject);
    if (!gridObject.provides(ProviderType.PARKING) || !superAllows) {
      return superAllows;
    }

    if (gridObject.provides(ProviderType.PARKING)) {
      int yPos = gridObject.getPosition().y;
      if (yPos < TowerConsts.LOBBY_FLOOR) {
        return true;
      }
    }

    return false;
  }
}
