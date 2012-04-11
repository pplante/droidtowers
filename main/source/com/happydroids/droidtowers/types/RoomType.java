/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers.types;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.happydroids.droidtowers.TowerConsts;
import com.happydroids.droidtowers.entities.*;
import com.happydroids.droidtowers.grid.GameGrid;
import com.happydroids.droidtowers.grid.GridPositionCache;

import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoomType extends GridObjectType {
  private boolean isLobby;
  private int populationMax;
  private int populationRequired;

  @Override
  public GridObject makeGridObject(GameGrid gameGrid) {
    if (provides == ProviderType.LOBBY) {
      return new Lobby(this, gameGrid);
    }

    return new Room(this, gameGrid);
  }

  @Override
  public boolean canShareSpace(GridObject gridObject) {
    return gridObject instanceof Elevator || gridObject instanceof Stair;
  }

  @Override
  public boolean canBeAt(GridObject gridObject) {
    if (provides == ProviderType.LOBBY) {
      return gridObject.getPosition().y == TowerConsts.LOBBY_FLOOR && checkForOverlap(gridObject);
    }

    return checkIfTouchingAnotherObject(gridObject) && checkForOverlap(gridObject);
  }

  @Override
  protected boolean checkForOverlap(GridObject gridObject) {
    Set<GridObject> objectsOverlapped = GridPositionCache.instance().getObjectsAt(gridObject.getPosition(), gridObject.getSize(), gridObject);
    for (GridObject object : objectsOverlapped) {
      if (!object.canShareSpace(gridObject)) {
        return false;
      }
    }

    return true;
  }

  public int getPopulationMax() {
    return populationMax;
  }

  public int getPopulationRequired() {
    return populationRequired;
  }
}
