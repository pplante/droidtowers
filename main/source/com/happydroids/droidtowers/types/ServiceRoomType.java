/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.badlogic.gdx.utils.Array;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.math.GridPoint;

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
    if (gridObject.provides(ProviderType.PARKING) && checkForOverlap(gridObject)) {
      if (gridObject.provides(ProviderType.PARKING_SPACE)) {
        // check single point to the left
        GridPoint objectGridPoint = gridObject.getPosition().cpy();
        if (checkPointForProviderType(gridObject, objectGridPoint.sub(1, 0), ProviderType.PARKING)) {
          return true;
        }
        // check single point to the right
        if (checkPointForProviderType(gridObject, objectGridPoint.add(2, 0), ProviderType.PARKING)) {
          return true;
        }

        return false;
      } else if (gridObject.provides(ProviderType.PARKING_RAMP)) {
        int yPos = gridObject.getPosition().y;
        if (yPos == TowerConsts.LOBBY_FLOOR) {
          return true;
        } else {
          GridPoint objectGridPoint = gridObject.getPosition().cpy();
          // check below
          Array<GridObject> gridObjects = gridObject.getGameGrid()
                                                  .positionCache()
                                                  .getObjectsAt(objectGridPoint.sub(0, 1), gridObject.getSize(), gridObject);
          for (GridObject object : gridObjects) {
            if (object.provides(ProviderType.PARKING_RAMP) && object.getPosition().x == gridObject.getPosition().x) {
              return true;
            }
          }
          // check above
          gridObjects = gridObject.getGameGrid()
                                .positionCache()
                                .getObjectsAt(objectGridPoint.add(0, 2), gridObject.getSize(), gridObject);
          for (GridObject object : gridObjects) {
            if (object.provides(ProviderType.PARKING_RAMP) && object.getPosition().x == gridObject.getPosition().x) {
              return true;
            }
          }

          return false;
        }
      }
    }

    return super.canBeAt(gridObject);
  }

  private boolean checkPointForProviderType(GridObject gridObject, GridPoint gridPoint, final ProviderType providerType) {
    Array<GridObject> gridObjects = gridObject.getGameGrid()
                                            .positionCache()
                                            .getObjectsAt(gridPoint, TowerConsts.SINGLE_POINT, gridObject);
    if (gridObjects.size > 0) {
      for (GridObject object : gridObjects) {
        if (object.provides(providerType)) {
          return true;
        }
      }
    }
    return false;
  }
}
